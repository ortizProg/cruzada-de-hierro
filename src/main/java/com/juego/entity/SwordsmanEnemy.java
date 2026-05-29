package com.juego.entity;

/**
 * Clase concreta de enemigo que representa a un Espadachín (Swordsman).
 */
public class SwordsmanEnemy extends Enemy {

    public SwordsmanEnemy(float x, float y) {
        super(x, y, EnemyFlyweightFactory.getFlyweight("Swordsman"));
    }

    @Override
    public void attack() {
        System.out.println("Swordsman strikes with his iron sword!");
    }
}
