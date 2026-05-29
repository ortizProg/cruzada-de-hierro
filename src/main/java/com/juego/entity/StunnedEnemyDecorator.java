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
    public float getAbsoluteX() {
        return decoratedEnemy.getAbsoluteX();
    }

    @Override
    public float getAbsoluteY() {
        return decoratedEnemy.getAbsoluteY();
    }

    @Override
    public com.juego.physics.Collider getCollider() {
        return decoratedEnemy.getCollider();
    }

    @Override
    public float getHealth() {
        return decoratedEnemy.getHealth();
    }

    @Override
    public void reduceHealth(float amount) {
        decoratedEnemy.reduceHealth(amount);
    }

    @Override
    public float getVy() {
        return decoratedEnemy.getVy();
    }

    @Override
    public void setVy(float vy) {
        decoratedEnemy.setVy(vy);
    }

    @Override
    public boolean isOnGround() {
        return decoratedEnemy.isOnGround();
    }

    @Override
    public void setOnGround(boolean onGround) {
        decoratedEnemy.setOnGround(onGround);
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
