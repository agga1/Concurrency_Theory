package com.agga1.dmesh;

import com.agga1.dmesh.parallelism.ConcurentBlockRunner;

class Application {

    public static void main(String args[]) {

        Executor e = new Executor(new ConcurentBlockRunner());
        e.start();
    }
}
