package com.juego.system;

/**
 * Bridge Pattern: Implementador.
 * Define la interfaz para capturar la entrada física del hardware.
 */
public interface InputDevice {
    String getPressedKey();
}
