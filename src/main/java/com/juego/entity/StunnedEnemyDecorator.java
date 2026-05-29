package com.juego.entity;

/**
 * Decorator Pattern: Añade dinámicamente el comportamiento de "Aturdimiento" 
 * a un enemigo (producido por el Mazo) sin alterar la clase base del enemigo.
 */
public class StunnedEnemyDecorator extends Enemy {
    private Enemy decoratedEnemy;
    private long stunEndTime;

    public StunnedEnemyDecorator(Enemy decoratedEnemy, long durationMs) {
        super(decoratedEnemy.getAbsoluteX(), decoratedEnemy.getAbsoluteY(), decoratedEnemy.getFlyweight());
        this.decoratedEnemy = decoratedEnemy;
        this.stunEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isStunned() {
        return System.currentTimeMillis() < stunEndTime;
    }

    @Override
    public void move(float dx, float dy) {
        if (isStunned()) {
            System.out.println("Decorator Active: " + flyweight.getType() + " is STUNNED and cannot move!");
        } else {
            decoratedEnemy.move(dx, dy);
        }
    }

    @Override
    public void attack() {
        if (isStunned()) {
            System.out.println("Decorator Active: " + flyweight.getType() + " is STUNNED and cannot attack!");
        } else {
            decoratedEnemy.attack();
        }
    }
}
