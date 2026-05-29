package com.juego.core;

import com.juego.entity.Enemy;
import com.juego.entity.EnemyDeathObserver;

/**
 * Singleton Pattern: Mantiene la puntuación global, el multiplicador de combo
 * y las vidas globales de la partida.
 * Observer Pattern: Concrete Observer que reacciona a la muerte de enemigos.
 */
public class ScoreManager implements EnemyDeathObserver {
    private static ScoreManager instance;

    private int score;
    private int comboCount;
    private long lastKillTime;
    private int lives;
    private final int BASE_ENEMY_POINTS = 100;
    private final long COMBO_EXPIRATION_MS = 10000; // 10 segundos

    private ScoreManager() {
        this.score = 0;
        this.comboCount = 0;
        this.lastKillTime = 0;
        this.lives = 3; // 3 Vidas globales
    }

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    @Override
    public void onEnemyKilled(Enemy enemy) {
        long currentTime = System.currentTimeMillis();
        
        // Verificar si el combo ha expirado antes de esta muerte
        checkComboExpiration(currentTime);
        
        // Incrementar contador de combo
        comboCount++;
        lastKillTime = currentTime;
        
        // Calcular multiplicador exponencial (base 3 kills)
        // multiplier = 2^(comboCount / 3)
        int multiplier = (int) Math.pow(2, comboCount / 3);
        int pointsAwarded = BASE_ENEMY_POINTS * multiplier;
        score += pointsAwarded;
        
        System.out.println("--- Enemigo Eliminado! ---");
        System.out.println("Kills seguidos (Combo): " + comboCount);
        System.out.println("Multiplicador: x" + multiplier);
        System.out.println("Puntos otorgados: " + pointsAwarded);
        System.out.println("Score total: " + score);
        System.out.println("--------------------------");
    }

    /**
     * Revisa si el combo de muertes ha expirado por tiempo.
     */
    public void update() {
        checkComboExpiration(System.currentTimeMillis());
    }

    private void checkComboExpiration(long currentTime) {
        if (comboCount > 0 && (currentTime - lastKillTime > COMBO_EXPIRATION_MS)) {
            System.out.println("¡Combo expirado! El multiplicador volvió a x1");
            comboCount = 0;
        }
    }

    public int getScore() {
        return score;
    }

    public int getComboCount() {
        long currentTime = System.currentTimeMillis();
        if (comboCount > 0 && (currentTime - lastKillTime > COMBO_EXPIRATION_MS)) {
            return 0;
        }
        return comboCount;
    }

    public int getLives() {
        return lives;
    }

    public void decrementLives() {
        if (lives > 0) {
            lives--;
            System.out.println("Vida perdida. Vidas restantes: " + lives);
            if (lives == 0) {
                System.out.println("GAME OVER - Se han agotado todas las vidas globales.");
                // En una implementación real, esto gatillaría la vista de fin de juego (GameOverView)
            }
        }
    }

    public void incrementLives() {
        if (lives < 3) {
            lives++;
            System.out.println("Vida recuperada. Vidas actuales: " + lives);
        }
    }

    public void reset() {
        this.score = 0;
        this.comboCount = 0;
        this.lastKillTime = 0;
        this.lives = 3;
    }
}
