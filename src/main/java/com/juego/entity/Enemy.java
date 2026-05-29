package com.juego.entity;

/**
 * Clase base para los Enemigos.
 * Hereda de Entity, aplicando el Single Responsibility Principle y Open/Closed Principle
 * para crear variaciones de enemigos (Zombies, Skeletons) sin modificar el núcleo.
 */
public abstract class Enemy extends Entity {
    protected EnemyFlyweight flyweight;
    private float vy = 0.0f;
    private boolean onGround = false;
    
    public Enemy(float x, float y, EnemyFlyweight flyweight) {
        super(x, y, flyweight.getBaseHealth(), flyweight.getBaseSpeed());
        this.flyweight = flyweight;
    }
    
    public EnemyFlyweight getFlyweight() {
        return flyweight;
    }
    
    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    /**
     * Método para activar el enemigo cuando sale del pool
     */
    public void spawn(float x, float y) {
        // En una implementación completa se requeriría actualizar x e y a través de un setter o move.
        // Debido al encapsulamiento, move es la forma estándar de alterar la posición, 
        // pero un reset de estado es preferible.
        this.increaseHealth(flyweight.getBaseHealth()); // Restablecer salud usando el flyweight
    }
}
