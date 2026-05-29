package com.juego.entity;

import com.juego.math.Vector2;

/**
 * Representa al Hero moviéndose en el suelo.
 */
public class RunningState implements HeroState {

    @Override
    public void enter(Hero hero) {
        System.out.println("Entering Running State");
    }

    @Override
    public void update(Hero hero) {
        // Lógica de desplazamiento por frame
    }

    @Override
    public void handleInput(Hero hero, String action) {
        if ("STOP".equals(action)) {
            hero.changeState(new IdleState());
        } else if ("MOVE_LEFT".equals(action)) {
            hero.setFacingDirection(new Vector2(-1, 0));
            hero.move(-1, 0);
        } else if ("MOVE_RIGHT".equals(action)) {
            hero.setFacingDirection(new Vector2(1, 0));
            hero.move(1, 0);
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
