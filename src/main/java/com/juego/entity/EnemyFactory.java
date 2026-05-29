package com.juego.entity;

/**
 * Factory Method Pattern: Interfaz base de la factoría de enemigos.
 */
public interface EnemyFactory {
    Enemy createEnemy(String type, float x, float y);
}
