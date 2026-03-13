package com.juego.entity;

/**
 * Clase base para los Enemigos.
 * Hereda de Entity, aplicando el Single Responsibility Principle y Open/Closed Principle
 * para crear variaciones de enemigos (Zombies, skeletons) sin modificar el núcleo.
 */
public abstract class Enemy extends Entity {
    
    public Enemy(float x, float y, float health, float speedMovement) {
        super(x, y, health, speedMovement);
    }
    
    /**
     * Método para activar el enemigo cuando sale del pool
     */
    public void spawn(float x, float y) {
        // En una implementación completa se requeriría actualizar x e y a través de un setter o move.
        // Debido al encapsulamiento, move es la forma estándar de alterar la posición, 
        // pero un reset de estado es preferible.
        this.increaseHealth(100); // Restablecer salud, por ejemplo
    }
}
