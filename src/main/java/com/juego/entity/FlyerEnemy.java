package com.juego.entity;

/**
 * Clase concreta que representa a un enemigo Volador (Flyer).
 */
public class FlyerEnemy extends Enemy {

    public FlyerEnemy(float x, float y) {
        super(x, y, EnemyFlyweightFactory.getFlyweight("Flyer"));
    }

    @Override
    public void attack() {
        System.out.println("Flyer dives to strike from above!");
    }
}
