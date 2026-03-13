package com.juego.weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Object Pool Pattern:
 * Minimiza la instanciación de memoria mediante `new` repetitivamente.
 * Pre-aloca múltiples elementos para ser reutilizados una y otra vez (Ej: Proyectiles).
 */
public abstract class ObjectPool<T> {
    private List<T> pool;
    private int maxPoolSize;

    public ObjectPool(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.pool = new ArrayList<>(maxPoolSize);
        initializePool();
    }

    private void initializePool() {
        for (int i = 0; i < maxPoolSize; i++) {
            pool.add(create());
        }
    }

    protected abstract T create();

    public T acquire() {
        if (!pool.isEmpty()) {
            return pool.remove(pool.size() - 1);
        }
        return create(); // Expandir si está exhausto
    }

    public void release(T object) {
        if (pool.size() < maxPoolSize) {
            pool.add(object);
        }
    }
}
