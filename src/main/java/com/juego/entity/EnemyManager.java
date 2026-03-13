package com.juego.entity;

import com.juego.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * EnemyManager gestiona el ciclo de vida de los enemigos utilizando
 * internamente conceptos de Object Pooling para evitar la creación y 
 * destrucción constante de instancias de Enemy.
 */
public class EnemyManager {
    private List<Enemy> enemyPool;
    private List<Enemy> activeEnemies;
    private int maxEnemyOnScreen;
    private int maxEnemyInPool;

    public EnemyManager(int maxEnemyOnScreen, int maxEnemyInPool) {
        this.maxEnemyOnScreen = maxEnemyOnScreen;
        this.maxEnemyInPool = maxEnemyInPool;
        this.enemyPool = new ArrayList<>();
        this.activeEnemies = new ArrayList<>();
    }

    public void addEnemies() {
        // Lógica para repoblar el pool si es necesario.
        // En una implementación real, se utilizaría una EnemyFactory para crear instancias.
    }

    public void spawnEnemies(Vector2 position) {
        if (activeEnemies.size() < maxEnemyOnScreen && !enemyPool.isEmpty()) {
            Enemy enemy = enemyPool.remove(enemyPool.size() - 1);
            enemy.spawn(position.x, position.y);
            activeEnemies.add(enemy);
            System.out.println("Enemy spawned at " + position.x + ", " + position.y);
        }
    }

    private List<Vector2> getSpawnPoints() {
        // Lógica para obtener puntos de aparición válidos en el mapa
        List<Vector2> points = new ArrayList<>();
        points.add(new Vector2(100, 100));
        points.add(new Vector2(200, 150));
        return points;
    }

    public void dieEnemy(Enemy enemy) {
        if (activeEnemies.remove(enemy)) {
            if (enemyPool.size() < maxEnemyInPool) {
                enemyPool.add(enemy);
            }
            System.out.println("Enemy returned to pool");
        }
    }
    
    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
}
