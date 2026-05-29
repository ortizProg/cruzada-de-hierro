package com.juego.entity;

/**
 * Clase concreta que representa a un Escudero (Shielder).
 */
public class ShielderEnemy extends Enemy {

    public ShielderEnemy(float x, float y) {
        super(x, y, EnemyFlyweightFactory.getFlyweight("Shielder"));
    }

    @Override
    public void attack() {
        System.out.println("Shielder pushes with his wooden shield!");
    }
}
