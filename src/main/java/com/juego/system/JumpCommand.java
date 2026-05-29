package com.juego.system;

import com.juego.entity.HeroFacade;

/**
 * Command concreto para ejecutar el Salto en el HeroFacade.
 */
public class JumpCommand implements ICommand {

    @Override
    public void execute(HeroFacade hero) {
        System.out.println("Command Executed: JUMP");
        hero.jump();
    }
}
