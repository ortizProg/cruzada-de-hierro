package com.juego.system;

import com.juego.entity.HeroFacade;

/**
 * Bridge Pattern: Abstracción.
 * Desacopla la lógica de entrada del control del personaje (baja latencia RNF2).
 */
public abstract class InputBridge {
    protected InputDevice inputDevice;

    protected InputBridge(InputDevice device) {
        this.inputDevice = device;
    }

    public void setInputDevice(InputDevice device) {
        this.inputDevice = device;
        System.out.println("Bridge Action: Changed physical input source.");
    }

    public abstract void handleInput(HeroFacade hero);
}
