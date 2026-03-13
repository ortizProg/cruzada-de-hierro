package com.juego.physics;

/**
 * Interfaz Collidable (Open/Closed Principle).
 * Permite que cualquier objeto en el juego pueda colisionar sin que el sistema
 * de físicas necesite conocer su tipo específico. 
 */
public interface Collidable {
    Collider getCollider();
    
    // El dueño expone su posición absoluta porque el collider es solo un desfase 
    // y debe usar x, y del dueño para un cálculo de mundo
    float getAbsoluteX();
    float getAbsoluteY();
}
