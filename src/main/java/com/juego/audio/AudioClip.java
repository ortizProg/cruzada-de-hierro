package com.juego.audio;

/**
 * Representa el sonido a reproducir.
 * Parte del Flyweight Pattern. En una implementación real esta clase tendría 
 * buffers de audio.
 */
public class AudioClip {
    private String resourcePath;

    public AudioClip(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public void play() {
        System.out.println("Playing sound: " + resourcePath);
    }
}
