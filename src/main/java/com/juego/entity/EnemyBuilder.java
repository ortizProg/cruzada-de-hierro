package com.juego.entity;

/**
 * Builder Pattern: Permite ensamblar y configurar niveles o configuraciones complejas
 * de los enemigos (ej. Caballero/Espadachín + Escudo + Vida extra).
 */
public class EnemyBuilder {
    private String type;
    private float x;
    private float y;
    private boolean hasShield;
    private float extraHealth;
    private float extraSpeed;

    public EnemyBuilder(String type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.hasShield = false;
        this.extraHealth = 0;
        this.extraSpeed = 0;
    }

    public EnemyBuilder addShield() {
        this.hasShield = true;
        return this;
    }

    public EnemyBuilder addExtraHealth(float extraHealth) {
        this.extraHealth += extraHealth;
        return this;
    }

    public EnemyBuilder addExtraSpeed(float extraSpeed) {
        this.extraSpeed += extraSpeed;
        return this;
    }

    public Enemy build() {
        // Instancia base usando la factoría
        BasicEnemyFactory factory = new BasicEnemyFactory();
        Enemy enemy = factory.createEnemy(type, x, y);
        
        // Aplicar personalizaciones del Builder
        if (extraHealth > 0) {
            enemy.increaseHealth(extraHealth);
        }
        
        if (hasShield) {
            System.out.println("Builder Applied: Added heavy shield to the enemy.");
        }
        
        if (extraSpeed > 0) {
            // El move() del enemigo usará su velocidad aumentada.
            System.out.println("Builder Applied: Increased movement speed by " + extraSpeed);
        }
        
        return enemy;
    }
}
