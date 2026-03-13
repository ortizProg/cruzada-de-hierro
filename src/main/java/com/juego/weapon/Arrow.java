package com.juego.weapon;

import com.juego.physics.Collidable;
import com.juego.physics.Collider;

/**
 * Arrow Implementa Collidable para poder interactuar en la física.
 * Demuestra que una bala puede ser Poolable y Collidable.
 */
public class Arrow implements Collidable {
    private float x, y;
    private float speed;
    private int damage;
    private Collider hitBox;
    private boolean active;

    public Arrow() {
        this.hitBox = new Collider(this, 0, 0, 10, 2);
        this.active = false;
    }

    public void activate(float startX, float startY, float speed, int damage) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.damage = damage;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void update() {
        if(active) {
            calculateTrajectory();
        }
    }

    private void calculateTrajectory() {
        this.x += speed;
    }

    @Override
    public Collider getCollider() {
        return hitBox;
    }

    @Override
    public float getAbsoluteX() {
        return x;
    }

    @Override
    public float getAbsoluteY() {
        return y;
    }
    
    public int getDamage() {
        return damage;
    }
}
