package com.juego.weapon;

import com.juego.math.Vector2;

/**
 * Bow implementa la Strategy IWeapon y usa un ObjectPool para despachar Arrow.
 * Demuestra "Dependency Injection" (DIP) inyectando o usando una piscina en el constructor.
 */
public class Bow implements IWeapon {
    private float lastAttackTime;
    private float speed;
    private ArrowPool arrowPool;
    private java.util.List<Arrow> activeProjectiles;

    // Dependency Inversion: Recibe dependencias externas
    public Bow(ArrowPool pool, java.util.List<Arrow> activeProjectiles) {
        this.arrowPool = pool;
        this.activeProjectiles = activeProjectiles;
        this.speed = 10f;
    }

    @Override
    public void attack(Vector2 position, Vector2 direction) {
        System.out.println("Bow fires an arrow at direction: " + direction.x + ", " + direction.y);
        // Uso de Object Pool (Evita overhead de GC)
        Arrow arrow = arrowPool.acquire();
        arrow.activate(position.x, position.y, direction.x, direction.y, speed, 15);
        if (activeProjectiles != null) {
            activeProjectiles.add(arrow);
        }
    }
}
