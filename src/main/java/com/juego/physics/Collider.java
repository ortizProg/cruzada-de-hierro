package com.juego.physics;

/**
 * Collider contiene solo el desfase (offset) y dimensiones locales.
 * Usa encapsulamiento (atributos privados, métodos de acceso).
 * Dependency Inversion: Depende de la abstracción Collidable, no de Entity
 * directmente.
 */
public class Collider {
    private Collidable owner;
    private float offsetX;
    private float offsetY;
    private float width;
    private float height;

    public Collider(Collidable owner, float offsetX, float offsetY, float width, float height) {
        this.owner = owner;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    // Calcula la posición X absoluta en el mundo pidiéndole el 'getAbsoluteX()' al
    // dueño.
    public float getWorldX() {
        return owner.getAbsoluteX() + offsetX;
    }

    public float getWorldY() {
        return owner.getAbsoluteY() + offsetY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Collidable getOwner() {
        return owner;
    }

    public boolean checkCollision(Collider other) {
        // AABB Collision detection
        return this.getWorldX() < other.getWorldX() + other.getWidth() &&
                this.getWorldX() + this.width > other.getWorldX() &&
                this.getWorldY() < other.getWorldY() + other.getHeight() &&
                this.getWorldY() + this.height > other.getWorldY();
    }
}
