package com.juego.entity;

/**
 * Representa al Hero en animación de ataque.
 * Evita o altera el movimiento mientras se ataca.
 */
public class AttackingState implements HeroState {
    private long attackStartTime;
    private long attackDurationMs = 250; // El ataque dura 250 ms

    @Override
    public void enter(Hero hero) {
        System.out.println("Entering Attacking State");
        this.attackStartTime = System.currentTimeMillis();
    }

    @Override
    public void update(Hero hero) {
        if (System.currentTimeMillis() - attackStartTime >= attackDurationMs) {
            hero.changeState(new IdleState());
        }
    }

    @Override
    public void handleInput(Hero hero, String action) {
        // Puede permitir encadenar combos o ignorar movimientos
    }

    @Override
    public void exit(Hero hero) {
        // Acciones al salir
    }
}
