package com.juego.physics;

/**
 * Observer Pattern:
 * Permite que los sistemas (DamageSystem, AudioSystem) escuchen y reaccionen a 
 * colisiones detectadas por el CollisionManager sin acoplarse mutuamente.
 */
public interface CollisionObserver {
    void onCollision(Collider source, Collider target);
}
