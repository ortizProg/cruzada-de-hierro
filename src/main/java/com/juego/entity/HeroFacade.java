package com.juego.entity;

import com.juego.math.Vector2;
import com.juego.weapon.IWeapon;

/**
 * Facade Pattern: Centraliza los subsistemas del jugador (movimiento, combate, físicas)
 * bajo una interfaz simplificada para otros componentes del juego.
 */
public class HeroFacade {
    private Hero hero;

    public HeroFacade(Hero hero) {
        this.hero = hero;
    }

    public void moveLeft() {
        hero.handleInput("MOVE_LEFT");
    }

    public void moveRight() {
        hero.handleInput("MOVE_RIGHT");
    }

    public void jump() {
        hero.handleInput("JUMP");
    }

    public void dash() {
        hero.handleInput("DASH");
    }

    public void stop() {
        hero.handleInput("STOP");
    }

    public void land() {
        hero.handleInput("LAND");
    }

    public void attack() {
        hero.handleInput("ATTACK");
    }

    public void attackDirection(Vector2 direction) {
        hero.attack(direction);
    }

    public void changeWeapon(IWeapon newWeapon) {
        hero.changeWeapon(newWeapon);
        System.out.println("Facade Action: Hero weapon swapped.");
    }

    public void update() {
        hero.updateState();
    }

    public Hero getHero() {
        return hero;
    }
}
