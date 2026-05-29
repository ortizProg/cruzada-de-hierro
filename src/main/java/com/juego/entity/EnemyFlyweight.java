package com.juego.entity;

/**
 * Flyweight Pattern: Almacena las propiedades intrínsecas compartidas de los enemigos
 * para optimizar el uso de memoria (RNF1).
 */
public class EnemyFlyweight {
    private final String type;
    private final float baseHealth;
    private final float baseSpeed;
    private final String spriteName;

    public EnemyFlyweight(String type, float baseHealth, float baseSpeed, String spriteName) {
        this.type = type;
        this.baseHealth = baseHealth;
        this.baseSpeed = baseSpeed;
        this.spriteName = spriteName;
    }

    public String getType() {
        return type;
    }

    public float getBaseHealth() {
        return baseHealth;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public String getSpriteName() {
        return spriteName;
    }
}
