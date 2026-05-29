package com.juego.weapon;

import com.juego.math.Vector2;

import com.juego.entity.Enemy;
import com.juego.entity.EnemyRegistry;

/**
 * Sword implementa la Strategy IWeapon.
 * Representa un arma rápida ideal para combatir hordas de enemigos.
 */
public class Sword implements IWeapon {
    private int damage;
    private float range;
    private long lastAttackTime;
    private long cooldownMs;
    private EnemyRegistry enemyRegistry;

    public Sword(EnemyRegistry registry) {
        this.damage = 20;
        this.range = 50.0f;
        this.cooldownMs = 300; // Cooldown rápido (300ms)
        this.lastAttackTime = 0;
        this.enemyRegistry = registry;
    }

    @Override
    public void attack(Vector2 position, Vector2 direction) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= cooldownMs) {
            lastAttackTime = currentTime;
            System.out.println("Swinging Sword! Fast slash at position: " + position.x + ", " + position.y);
            
            // Buscar enemigos en rango
            if (enemyRegistry != null) {
                for (Enemy enemy : enemyRegistry.getEnemies()) {
                    float ex = enemy.getAbsoluteX();
                    float ey = enemy.getAbsoluteY();
                    float dx = ex - position.x;
                    float dy = ey - position.y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    
                    if (dist <= range) {
                        // Comprobar si está frente al héroe
                        float dot = dx * direction.x + dy * direction.y;
                        if (dot >= 0) {
                            enemy.reduceHealth(damage);
                            System.out.println("Sword Hit: Swordsman slashed " + enemy.getFlyweight().getType() + " for 20 damage.");
                        }
                    }
                }
            }
        }
    }

    public int getDamage() {
        return damage;
    }

    public float getRange() {
        return range;
    }
}
