package com.juego.weapon;

import com.juego.math.Vector2;

import com.juego.entity.Enemy;
import com.juego.entity.EnemyRegistry;
import com.juego.entity.StunnedEnemyDecorator;

/**
 * Hammer implementa la Strategy IWeapon.
 * Es un arma lenta pero devastadora, capaz de romper escudos y aturdir a los enemigos.
 */
public class Hammer implements IWeapon {
    private int damage;
    private float range;
    private long lastAttackTime;
    private long cooldownMs;
    private EnemyRegistry enemyRegistry;

    public Hammer(EnemyRegistry registry) {
        this.damage = 50;
        this.range = 60.0f;
        this.cooldownMs = 1200; // Lento (1200ms de cooldown)
        this.lastAttackTime = 0;
        this.enemyRegistry = registry;
    }

    @Override
    public void attack(Vector2 position, Vector2 direction) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= cooldownMs) {
            lastAttackTime = currentTime;
            System.out.println("Slamming Hammer! Heavy overhead strike.");
            
            // Buscar enemigos en rango y decorarlos si colisionan
            if (enemyRegistry != null) {
                java.util.List<Enemy> list = enemyRegistry.getEnemies();
                for (int i = 0; i < list.size(); i++) {
                    Enemy enemy = list.get(i);
                    float ex = enemy.getAbsoluteX();
                    float ey = enemy.getAbsoluteY();
                    float dx = ex - position.x;
                    float dy = ey - position.y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    
                    if (dist <= range) {
                        float dot = dx * direction.x + dy * direction.y;
                        if (dot >= 0) {
                            enemy.reduceHealth(damage);
                            System.out.println("Hammer Hit: Devastating strike on " + enemy.getFlyweight().getType());
                            
                            // Decorador estructural: envolver al enemigo en StunnedEnemyDecorator
                            if (!(enemy instanceof StunnedEnemyDecorator)) {
                                StunnedEnemyDecorator decorated = new StunnedEnemyDecorator(enemy, 3000); // 3 segundos de stun
                                list.set(i, decorated);
                                System.out.println("Decorator Applied: Enemy is now STUNNED!");
                            }
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
