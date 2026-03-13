package com.juego.weapon;

/**
 * Bow implementa la Strategy IWeapon y usa un ObjectPool para despachar Arrow.
 * Demuestra "Dependency Injection" (DIP) inyectando o usando una piscina en el constructor.
 */
public class Bow implements IWeapon {
    private float lastAttackTime;
    private float speed;
    private ArrowPool arrowPool;

    // Dependency Inversion: Recibe dependencias externas
    public Bow(ArrowPool pool) {
        this.arrowPool = pool;
        this.speed = 10f;
    }

    @Override
    public void attack() {
        System.out.println("Bow fires an arrow!");
        // Uso de Object Pool (Evita overhead de GC)
        Arrow arrow = arrowPool.acquire();
        arrow.activate(0, 0, speed, 15);
    }
}
