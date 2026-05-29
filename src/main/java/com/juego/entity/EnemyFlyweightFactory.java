package com.juego.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Flyweight Factory: Administra y almacena en caché las instancias de EnemyFlyweight.
 */
public class EnemyFlyweightFactory {
    private static final Map<String, EnemyFlyweight> cache = new HashMap<>();

    public static EnemyFlyweight getFlyweight(String type) {
        if (!cache.containsKey(type)) {
            EnemyFlyweight flyweight;
            switch (type.toUpperCase()) {
                case "SWORDSMAN":
                    flyweight = new EnemyFlyweight("Swordsman", 50.0f, 2.0f, "swordsman_sprite.png");
                    break;
                case "SHIELDER":
                    flyweight = new EnemyFlyweight("Shielder", 100.0f, 1.2f, "shielder_sprite.png");
                    break;
                case "FLYER":
                    flyweight = new EnemyFlyweight("Flyer", 30.0f, 3.0f, "flyer_sprite.png");
                    break;
                default:
                    flyweight = new EnemyFlyweight("Generic", 40.0f, 1.5f, "generic_sprite.png");
            }
            cache.put(type, flyweight);
            System.out.println("Flyweight created and cached for enemy type: " + type);
        }
        return cache.get(type);
    }
}
