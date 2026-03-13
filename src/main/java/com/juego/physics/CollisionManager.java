package com.juego.physics;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern: Sujeto (Subject).
 * CollisionManager detecta e informa de colisiones a sus observadores, 
 * sin restar vida de las entidades directamente (manteniendo SRP).
 */
public class CollisionManager {
    private SpatialGrid grid;
    private List<CollisionObserver> observers;

    public CollisionManager(int cellSize) {
        this.grid = new SpatialGrid(cellSize);
        this.observers = new ArrayList<>();
    }

    public void addObserver(CollisionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(CollisionObserver observer) {
        observers.remove(observer);
    }

    private void notifyCollision(Collider source, Collider target) {
        for (CollisionObserver observer : observers) {
            observer.onCollision(source, target);
        }
    }

    void checkProjectilesVsEnemies(List<Collider> projectiles, List<Collider> enemies) {
        grid.clear();
        for (Collider enemy : enemies) {
            grid.insert(enemy);
        }

        for (Collider proj : projectiles) {
            List<Collider> nearbyEnemies = grid.getNearbyColliders(proj);
            for (Collider enemy : nearbyEnemies) {
                if (proj.checkCollision(enemy)) {
                    notifyCollision(proj, enemy);
                }
            }
        }
    }

    void checkHeroVsEnemies(Collider hero, List<Collider> enemies) {
        grid.clear();
        for (Collider enemy : enemies) {
            grid.insert(enemy);
        }
        
        List<Collider> nearbyEnemies = grid.getNearbyColliders(hero);
        for (Collider enemy : nearbyEnemies) {
            if (hero.checkCollision(enemy)) {
                notifyCollision(hero, enemy);
            }
        }
    }

    public void update(Collider hero, List<Collider> enemies, List<Collider> projectiles) {
        checkHeroVsEnemies(hero, enemies);
        checkProjectilesVsEnemies(projectiles, enemies);
    }
}
