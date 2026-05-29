package com.juego.entity;

import com.juego.math.Vector2;

/**
 * Representa al Hero en el aire (saltando o cayendo).
 * Soporta doble salto.
 */
public class JumpingState implements HeroState {

    @Override
    public void enter(Hero hero) {
        System.out.println("Entering Jumping State");
    }

    @Override
    public void update(Hero hero) {
        // En una simulación física, aquí se aplicaría gravedad
    }

    @Override
    public void handleInput(Hero hero, String action) {
        if ("LAND".equals(action)) {
            hero.changeState(new IdleState());
        } else if ("JUMP".equals(action)) {
            // Doble salto
            if (hero.getJumps() < 2) {
                hero.jump();
                System.out.println("Double jump executed!");
                // Se mantiene en JumpingState
            }
        } else if ("MOVE_LEFT".equals(action)) {
            hero.setFacingDirection(new Vector2(-1, 0));
            hero.move(-0.8f, 0); // Control aéreo reducido
        } else if ("MOVE_RIGHT".equals(action)) {
            hero.setFacingDirection(new Vector2(1, 0));
            hero.move(0.8f, 0);  // Control aéreo reducido
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
