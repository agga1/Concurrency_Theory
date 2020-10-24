package com.agga1.dmesh.parallelism;

import com.agga1.dmesh.production.IProduction;

public interface BlockRunner {

    //starts all threads
    public void startAll();

    //adds a thread to poll
    public void addThread(IProduction pThread);
}
