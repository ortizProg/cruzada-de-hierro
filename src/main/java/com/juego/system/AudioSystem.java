package com.juego.system;

import com.juego.physics.Collider;
import com.juego.physics.CollisionObserver;
import com.juego.audio.AudioManager;
import com.juego.entity.Entity;
import com.juego.weapon.Arrow;

/**
 * Observer Pattern: Concrete Observer.
 * Reacciona cuando CollisionManager detecta una colisión, reproduciendo el sonido
 * correspondiente. Interactúa con el AudioManager con Dependency Injection.
 */
public class AudioSystem implements CollisionObserver {
    
    private AudioManager audioManager;

    public AudioSystem(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void onCollision(Collider source, Collider target) {
        // En base al tipo de colisión se puede reproducir distintos audios.
        // Utilizando instanceof demostrativo (en un diseño más complejo se usaría Double Dispatch o IDs).
        if (source.getOwner() instanceof Arrow && target.getOwner() instanceof Entity) {
            audioManager.playSound("arrow_hit.ogg");
        } else {
            audioManager.playSound("generic_impact.ogg");
        }
    }
}
