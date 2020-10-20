const { performance } = require('perf_hooks');

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
 }

// Conductor implemented as counting semahpore
var Conductor = function (max) {
    this.count = 0;
    this.queue = [];
    this.max = max;
    return this;
}
Conductor.prototype.popQueue = function() {
    if (this.queue.length > 0 && this.count < this.max){
      this.count++;
      let promise = this.queue.shift();
      promise.resolve();
    }
  }
Conductor.prototype.acquire = function() {

    if(this.count < this.max) {
      this.count++;
      return new Promise(resolve => {resolve();});
    } else {
      return new Promise((resolve, err) => {
        this.queue.push({resolve: resolve, err: err});
      });
    }
}
Conductor.prototype.release = function() {
    this.count--;
    this.popQueue();
}

// Fork implementation
var Fork = function(id) {
    this.state = 0;
    this.id = id;
    return this;
}
Fork.prototype.acquire = function(c = 1) { 
    if(this.tryAcquire()) {
        return true;
    } else {
        c = c*2;
        var msToWait = getRandomInt(0, c-1);
        return new Promise( resolve => {
            setTimeout(() => resolve(this.acquire(c)), msToWait);
        });
    }  
}
Fork.prototype.tryAcquire = function() { 
    if(this.state == 0){
        this.state = -1;
        return true;
    }else{
        return false;
    }
}
Fork.prototype.release = function() { 
    this.state = 0; 
}

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    this.waitElapsed = 0;
    this.eatCount = 0;
    this.start = 0;
    return this;
}
Philosopher.prototype.getAccumulatedTime = function(){
    if(this.start==0) return 0;
    var accumulated = performance.now() - this.start;
    // console.log(this.id+" acc "+accumulated);
    this.start = 0;
    return accumulated;
}
Philosopher.prototype.getAverageWait = function(N=0) {
    if(this.start != 0){
        this.waitElapsed += this.getAccumulatedTime();
        this.start = performance.now();
    }
    // console.log(this.eatCount+", "+this.waitElapsed);
    if(N!=0){ // if N given, log on console along with number of philosophers
        console.log(Math.round(this.waitElapsed/Math.max(this.eatCount, 1))+","+N+","+this.id);
    }
    return Math.round(this.waitElapsed/Math.max(this.eatCount, 1));
}
Philosopher.prototype.eat = function() {
    return new Promise(resolve => {
        console.log("-------"+this.id+" eating");
        setTimeout(() =>{
             resolve(true)}, getRandomInt(500, 1000));
      });
}
Philosopher.prototype.think = function() {
    return new Promise(resolve => {
        setTimeout(() => resolve(true), getRandomInt(200, 500));
      });
}
Philosopher.prototype.startNaive = async function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2;

    for(var i=0; i<count; i++){
        this.start = performance.now();
        await forks[f1].acquire();
        await forks[f2].acquire();
        this.waitElapsed += this.getAccumulatedTime();
        this.eatCount ++;
        await this.eat();
        forks[f1].release();
        forks[f2].release();
        await this.think();

    }
}
Philosopher.prototype.startAsym = async function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2;

    for(var i=0; i<count; i++){
        this.start = performance.now();
        if(this.id%2==0){
            await forks[f2].acquire();
            await forks[f1].acquire();    
        }else{
            await forks[f1].acquire();
            await forks[f2].acquire();
        }
        this.waitElapsed += this.getAccumulatedTime();
        this.eatCount ++;
        await this.eat();
        forks[f1].release();
        forks[f2].release();
        await this.think();
    }
}
Philosopher.prototype.startConductor = async function(count, conductor) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2;
    
        for(var i=0; i<count; i++){
            await this.think();
            this.start = performance.now();
            await conductor.acquire();
            await forks[f1].acquire();
            await forks[f2].acquire(); 
            this.waitElapsed += this.getAccumulatedTime();
            this.eatCount ++;   
            await this.eat();
            forks[f1].release();
            forks[f2].release();
            conductor.release();
            console.log(this.id+" released >>>>>>>>");

            // await this.think();
        }
}
async function bothOrNone(fork1, fork2, cb=1){
    var gotLeft = fork1.tryAcquire();
    if(gotLeft){
        if(fork2.tryAcquire()) return true;
    }
    if(gotLeft) fork1.release()
    var msToWait = cb == 1 ? 1 : getRandomInt(0, cb-1);
    return new Promise( resolve => {
        setTimeout(() => resolve(bothOrNone(fork1, fork2, cb*2)), msToWait);
    });
}
Philosopher.prototype.startBothOrNone = async function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2;

    for(var i=0; i<count; i++){
        this.start = performance.now();
        await bothOrNone(forks[f1], forks[f2]);
        this.waitElapsed += this.getAccumulatedTime();
        this.eatCount ++;
        await this.eat();
        forks[f1].release();
        forks[f2].release();
        await this.think();
    }
}

const N = 7;
const count = 50;
var forks = [];
var philosophers = []
for (var i = 0; i < N; i++) forks.push(new Fork(i));
for (var i = 0; i < N; i++) philosophers.push(new Philosopher(i, forks));

function runNaive(){
    for (var i = 0; i < N; i++) 
        philosophers[i].startNaive(count);
}
function runAsym(){
    for (var i = 0; i < N; i++) 
        philosophers[i].startAsym(count);
}
function runConductor(){
    var conductor = new Conductor(N-1);
    for (var i = 0; i < N; i++) 
        philosophers[i].startConductor(count, conductor);
}
function runBothOrNone(){
    for (var i = 0; i < N; i++) 
        philosophers[i].startBothOrNone(count);
}

async function main(){
    runNaive();
    // log average waiting times each 10 s
    while(true){
    await sleep(10000);
    for (var i = 0; i < N; i++) 
        philosophers[i].getAverageWait(N);  
    }
}
main();
