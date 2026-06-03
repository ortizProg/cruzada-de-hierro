package com.juego.entity;

/**
 * Representa al Hero en un dash (suelo o aéreo) a alta velocidad.
 * Este estado es de corta duración y generalmente invulnerable o ignora gravedad.
 */
public class DashingState implements HeroState {
    private long dashStartTime;
    private long dashDurationMs = 200; // El dash dura 200 ms
    private com.juego.math.Vector2 dashDirection;

    @Override
    public void enter(Hero hero) {
        System.out.println("Entering Dashing State");
        this.dashStartTime = System.currentTimeMillis();
        // Bloquear la dirección de dash al inicio del dash
        this.dashDirection = new com.juego.math.Vector2(hero.getFacingDirection().x, hero.getFacingDirection().y);
    }

    @Override
    public void update(Hero hero) {
        // Mover a alta velocidad en la dirección bloqueada al inicio del dash
        hero.move(dashDirection.x * 3.0f, dashDirection.y * 3.0f);
        
        if (System.currentTimeMillis() - dashStartTime >= dashDurationMs) {
            System.out.println("Dash finished");
            // Por simplicidad, volvemos a Idle, la física real determinaría si cae o corre
            hero.changeState(new IdleState());
        }
    }

    @Override
    public void handleInput(Hero hero, String action) {
        // En DashingState se suelen ignorar otras entradas de movimiento para no interrumpir el dash
    }

    @Override
    public void exit(Hero hero) {
        // Acciones al salir
    }
}
