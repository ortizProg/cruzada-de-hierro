package com.juego.system;

import com.juego.entity.HeroFacade;

/**
 * Command concreto para iniciar movimiento a la derecha.
 */
public class MoveRightCommand implements ICommand {

    @Override
    public void execute(HeroFacade hero) {
        System.out.println("Command Executed: MOVE_RIGHT");
        hero.moveRight();
    }
}
