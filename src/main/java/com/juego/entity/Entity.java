package com.juego.entity;

import com.juego.physics.Collidable;
import com.juego.physics.Collider;

/**
 * Abstract Class Entity
 * Single Responsibility Principle (SRP): Entidad base que maneja su estado y posición matemática,
 * dejando los gráficos, audio y detección de colisiones a otros sistemas.
 */
public abstract class Entity implements Collidable {
    private float x;
    private float y;
    private float health;
    private float speedMovement;
    private Collider bodyCollider;

    public Entity(float x, float y, float health, float speedMovement) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speedMovement = speedMovement;
        // Asignación de un collider por defecto con offset(0,0)
        this.bodyCollider = new Collider(this, 0, 0, 32, 32); 
    }

    public void reduceHealth(float amount) {
        this.health -= amount;
        if (this.health < 0) this.health = 0;
    }

    public void increaseHealth(float amount) {
        this.health += amount;
    }

    public abstract void attack();

    public void move(float dx, float dy) {
        this.x += dx * speedMovement;
        this.y += dy * speedMovement;
    }

    // Implementación de Collidable
    @Override
    public Collider getCollider() {
        return bodyCollider;
    }

    @Override
    public float getAbsoluteX() {
        return x;
    }

    @Override
    public float getAbsoluteY() {
        return y;
    }
    
    // Método default getter para health o internals si es necesario (Encapsulamiento)
    float getHealth() {
        return this.health;
    }
}
