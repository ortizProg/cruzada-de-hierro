package com.juego.system;

import com.juego.entity.HeroFacade;

/**
 * Bridge Pattern: Abstracción Refinada.
 * Traduce las señales físicas del implementador a Comandos de ejecución rápida.
 */
public class GameInputHandler extends InputBridge {

    public GameInputHandler(InputDevice device) {
        super(device);
    }

    @Override
    public void handleInput(HeroFacade hero) {
        String key = inputDevice.getPressedKey();
        if (key == null) return;

        ICommand command = null;
        switch (key.toUpperCase()) {
            case "A":
            case "LEFT_STICK_LEFT":
                command = new MoveLeftCommand();
                break;
            case "D":
            case "LEFT_STICK_RIGHT":
                command = new MoveRightCommand();
                break;
            case "SPACE":
            case "BUTTON_A":
                command = new JumpCommand();
                break;
            case "SHIFT":
            case "BUTTON_X":
                command = new DashCommand();
                break;
            case "J":
            case "RIGHT_TRIGGER":
                command = new AttackCommand();
                break;
            default:
                // Sin mapear
        }

        if (command != null) {
            command.execute(hero);
        }
    }
}
