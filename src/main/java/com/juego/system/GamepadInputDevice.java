package com.juego.system;

/**
 * Bridge Pattern: Concret Implementor.
 * Captura la entrada proveniente de un gamepad/mando.
 */
public class GamepadInputDevice implements InputDevice {
    private String currentButton;

    public GamepadInputDevice(String button) {
        this.currentButton = button;
    }

    public void setPressedButton(String button) {
        this.currentButton = button;
    }

    @Override
    public String getPressedKey() {
        return currentButton;
    }
}
