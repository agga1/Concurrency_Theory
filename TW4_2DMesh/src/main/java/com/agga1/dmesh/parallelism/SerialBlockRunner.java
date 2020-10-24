package com.agga1.dmesh.parallelism;

import com.agga1.dmesh.production.IProduction;

public class SerialBlockRunner extends AbstractBlockRunner {

    private final MyLock lock = new MyLock();

    @Override
    void runOne(IProduction _pOne) {
        _pOne.injectRefs(lock);
        _pOne.start();
        lock.unlock();
    }

    @Override
    void wakeAll() {
        //do nothing
    }

}
