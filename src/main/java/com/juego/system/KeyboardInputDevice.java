package com.juego.system;

/**
 * Bridge Pattern: Concret Implementor.
 * Captura la entrada proveniente del teclado.
 */
public class KeyboardInputDevice implements InputDevice {
    private String currentKey;

    public KeyboardInputDevice(String key) {
        this.currentKey = key;
    }

    public void setPressedKey(String key) {
        this.currentKey = key;
    }

    @Override
    public String getPressedKey() {
        return currentKey;
    }
}
