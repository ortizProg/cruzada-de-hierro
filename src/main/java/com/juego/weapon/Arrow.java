package com.juego.weapon;

import com.juego.physics.Collidable;
import com.juego.physics.Collider;

/**
 * Arrow Implementa Collidable para poder interactuar en la física.
 * Demuestra que una bala puede ser Poolable y Collidable.
 */
public class Arrow implements Collidable {
    private float x, y;
    private float dirX, dirY;
    private float speed;
    private int damage;
    private Collider hitBox;
    private boolean active;

    public Arrow() {
        this.hitBox = new Collider(this, 0, 0, 10, 10);
        this.active = false;
    }

    public void activate(float startX, float startY, float dirX, float dirY, float speed, int damage) {
        this.x = startX;
        this.y = startY;
        this.dirX = dirX;
        this.dirY = dirY;
        
        // Normalizar la dirección
        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (length > 0) {
            this.dirX = dirX / length;
            this.dirY = dirY / length;
        } else {
            this.dirX = 1;
            this.dirY = 0;
        }

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
        this.x += dirX * speed;
        this.y += dirY * speed;
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
