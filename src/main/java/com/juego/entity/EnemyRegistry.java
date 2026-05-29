package com.juego.entity;

import java.util.List;

/**
 * Interface para desacoplar el acceso a la lista de enemigos activos del juego (DIP).
 */
public interface EnemyRegistry {
    List<Enemy> getEnemies();
}
