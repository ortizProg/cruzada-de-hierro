package com.juego.entity;

/**
 * State Pattern: Interface para los estados del Hero.
 * Encapsula la lógica de comportamiento físico e input para cada estado del personaje.
 */
public interface HeroState {
    void enter(Hero hero);
    void update(Hero hero);
    void handleInput(Hero hero, String action);
    void exit(Hero hero);
}
