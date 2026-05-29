package com.juego.entity;

import com.juego.weapon.IWeapon;
import com.juego.math.Vector2;

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
    private Vector2 facingDirection;
    private HeroState currentState;

    public Hero(float x, float y, float health, float speedMovement, IWeapon initialWeapon) {
        super(x, y, health, speedMovement);
        this.equippedWeapon = initialWeapon;
        this.currentJumps = 0;
        this.dashDelay = 1000; // 1000ms
        this.facingDirection = new Vector2(1, 0); // Por defecto mirando a la derecha
        this.currentState = new IdleState();
        this.currentState.enter(this);
    }

    public void changeState(HeroState newState) {
        if (this.currentState != null) {
            this.currentState.exit(this);
        }
        this.currentState = newState;
        this.currentState.enter(this);
    }

    public void handleInput(String action) {
        if (this.currentState != null) {
            this.currentState.handleInput(this, action);
        }
    }

    public HeroState getCurrentState() {
        return currentState;
    }

    public void updateState() {
        if (this.currentState != null) {
            this.currentState.update(this);
        }
    }

    public void changeWeapon(IWeapon newWeapon) {
        this.equippedWeapon = newWeapon;
    }

    public Vector2 getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Vector2 direction) {
        this.facingDirection = direction;
    }

    @Override
    public void attack() {
        if (equippedWeapon != null) {
            Vector2 position = new Vector2(getAbsoluteX(), getAbsoluteY());
            equippedWeapon.attack(position, facingDirection); // Call strategy interface method
        }
    }

    public void attack(Vector2 targetDirection) {
        if (equippedWeapon != null) {
            Vector2 position = new Vector2(getAbsoluteX(), getAbsoluteY());
            equippedWeapon.attack(position, targetDirection);
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
