package com.juego.entity;

/**
 * Observer Pattern: Interfaz para observar la muerte de los enemigos.
 */
public interface EnemyDeathObserver {
    void onEnemyKilled(Enemy enemy);
}
