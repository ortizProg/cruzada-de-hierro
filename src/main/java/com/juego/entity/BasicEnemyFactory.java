package com.juego.entity;

/**
 * Factory Method Pattern: Concrete Factory que genera diferentes tipos de enemigos.
 */
public class BasicEnemyFactory implements EnemyFactory {

    @Override
    public Enemy createEnemy(String type, float x, float y) {
        if (type == null) {
            return new SwordsmanEnemy(x, y);
        }
        
        switch (type.toUpperCase()) {
            case "SWORDSMAN":
                return new SwordsmanEnemy(x, y);
            case "SHIELDER":
                return new ShielderEnemy(x, y);
            case "FLYER":
                return new FlyerEnemy(x, y);
            default:
                return new SwordsmanEnemy(x, y);
        }
    }
}
