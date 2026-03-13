package com.juego.weapon;

/**
 * Instancia concreta del Object Pool para flechas.
 */
public class ArrowPool extends ObjectPool<Arrow> {
    
    public ArrowPool(int maxPoolSize) {
        super(maxPoolSize);
    }

    @Override
    protected Arrow create() {
        return new Arrow();
    }
}
