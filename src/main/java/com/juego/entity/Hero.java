package com.juego.entity;

import com.juego.weapon.IWeapon;

/**
 * Clase Hero.
 * Implementa Single Responsibility: solo maneja su estado interno y su
 * posición.
 * Usa Strategy Pattern para el combate al delegar 'attack()' al arma equipada.
 */
public class Hero extends Entity {
    private IWeapon equippedWeapon;
    private int currentJumps;
    private int dashDelay;
    private long lastDashTime;

    public Hero(float x, float y, float health, float speedMovement, IWeapon initialWeapon) {
        super(x, y, health, speedMovement);
        this.equippedWeapon = initialWeapon;
        this.currentJumps = 0;
        this.dashDelay = 1000; // 1000ms
    }

    public void changeWeapon(IWeapon newWeapon) {
        this.equippedWeapon = newWeapon;
    }

    @Override
    public void attack() {
        if (equippedWeapon != null) {
            equippedWeapon.attack(); // Call strategy interface method
        }
    }

    public void jump() {
        if (currentJumps >= 2)
            return;
        incrementJump();
    }

    public void dash() {
        // Ejecucíones de dash
        if (lastDashTime != 0 && System.currentTimeMillis() - lastDashTime < dashDelay)
            return;
        lastDashTime = System.currentTimeMillis();
        System.out.println("Dash ejecutado");
    }

    // Default o private modifiers required by Encapsulation rule
    public void resetJumps() {
        this.currentJumps = 0;
    }

    private void incrementJump() {
        this.currentJumps++;
    }

    public int getJumps() {
        return this.currentJumps;
    }
}
