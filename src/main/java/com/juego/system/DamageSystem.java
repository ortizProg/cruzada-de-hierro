package com.juego.system;

import com.juego.physics.Collider;
import com.juego.physics.CollisionObserver;
import com.juego.entity.Entity;

/**
 * Observer Pattern: Concrete Observer.
 * Reacciona cuando CollisionManager detecta que el Collider del arma/personaje
 * ha impactado, reduciendo la salud de los involucrados.
 */
public class DamageSystem implements CollisionObserver {

    @Override
    public void onCollision(Collider source, Collider target) {
        // Validación de ejemplo si un proyectil golpea una entidad
        if (target.getOwner() instanceof Entity) {
            Entity targetEntity = (Entity) target.getOwner(); // Esto requeriría acceder al Owner del collider
            targetEntity.reduceHealth(10);
        }
        // Nota: en una implementación real, se accede al propietario del collider
        // que debería proporcionar una forma de aplicar daño.
    }
}
