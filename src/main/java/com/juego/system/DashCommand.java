package com.juego.system;

import com.juego.entity.HeroFacade;

/**
 * Command concreto para ejecutar el Dash en el HeroFacade.
 */
public class DashCommand implements ICommand {

    @Override
    public void execute(HeroFacade hero) {
        System.out.println("Command Executed: DASH");
        hero.dash();
    }
}
