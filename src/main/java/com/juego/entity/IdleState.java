package com.juego.entity;

/**
 * Representa al Hero quieto, esperando entrada de movimiento, salto o ataque.
 */
public class IdleState implements HeroState {

    @Override
    public void enter(Hero hero) {
        System.out.println("Entering Idle State");
        hero.resetJumps();
    }

    @Override
    public void update(Hero hero) {
        // En reposo no se desplaza de forma activa.
    }

    @Override
    public void handleInput(Hero hero, String action) {
        if ("MOVE_LEFT".equals(action) || "MOVE_RIGHT".equals(action)) {
            hero.changeState(new RunningState());
        } else if ("JUMP".equals(action)) {
            hero.jump();
            hero.changeState(new JumpingState());
        } else if ("DASH".equals(action)) {
            hero.dash();
            hero.changeState(new DashingState());
        } else if ("ATTACK".equals(action)) {
            hero.attack();
            hero.changeState(new AttackingState());
        }
    }

    @Override
    public void exit(Hero hero) {
        // Acciones al salir
    }
}
