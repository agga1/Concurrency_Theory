package com.agga1.dmesh.production;

import com.agga1.dmesh.parallelism.MyLock;

public abstract class AbstractProduction<P> implements IProduction<P> {

    private MyLock lock;
    private final PThread thread = new PThread();
    private final P obj;
    private P result;

    public AbstractProduction(P _obj) {
        this.obj = _obj;
    }

    @Override
    public P getObj() {
        return this.result;
    }

//run the thread
    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void join() throws InterruptedException {
        thread.join();
    }

    @Override
    public void injectRefs(MyLock _lock) {
        this.lock = _lock;
    }

    private class PThread extends Thread {

        @Override
        public void run() {
            lock.lock();
            result = apply(obj);
        }
    }
}
