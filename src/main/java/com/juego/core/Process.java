package com.juego.core;

public abstract class Process {
    private Runnable process;

    public Process(Runnable process) {
        this.process = process;
    }

    public void complete() {
        if (process != null) {
            process.run();
        }
    }
}
