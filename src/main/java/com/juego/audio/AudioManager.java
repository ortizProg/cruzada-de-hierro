package com.juego.audio;

import java.util.HashMap;
import java.util.Map;

/**
 * Flyweight Pattern:
 * Administra y almacena en caché los archivos de sonido en memoria.
 * Asegura que un archivo de sonido (ej. "impact.mp3") se cargue una única vez 
 * a la vez que proporciona instancias ligeras o referencias a múltiples consumidores.
 */
public class AudioManager {
    private Map<String, AudioClip> audioCache;

    public AudioManager() {
        this.audioCache = new HashMap<>();
    }

    public AudioClip getAudio(String soundId) {
        if (!audioCache.containsKey(soundId)) {
            // Carga diferida (Lazy loading) del audio
            System.out.println("Loading audio file into memory: " + soundId);
            audioCache.put(soundId, new AudioClip(soundId));
        }
        return audioCache.get(soundId);
    }
    
    public void playSound(String soundId) {
        AudioClip clip = getAudio(soundId);
        clip.play();
    }
}
