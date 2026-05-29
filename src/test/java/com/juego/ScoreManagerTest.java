package com.juego;

import com.juego.core.ScoreManager;
import com.juego.entity.Enemy;
import com.juego.entity.SwordsmanEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {

    @BeforeEach
    void setUp() {
        ScoreManager.getInstance().reset();
    }

    @Test
    void testSingletonInstance() {
        ScoreManager instance1 = ScoreManager.getInstance();
        ScoreManager instance2 = ScoreManager.getInstance();
        assertSame(instance1, instance2, "ScoreManager debe ser un Singleton único");
    }

    @Test
    void testInitialState() {
        ScoreManager manager = ScoreManager.getInstance();
        assertEquals(3, manager.getLives(), "Debe iniciar con 3 vidas");
        assertEquals(0, manager.getScore(), "Debe iniciar con 0 puntos");
        assertEquals(0, manager.getComboCount(), "Debe iniciar con combo en 0");
    }

    @Test
    void testLivesManagement() {
        ScoreManager manager = ScoreManager.getInstance();
        manager.decrementLives();
        assertEquals(2, manager.getLives(), "Debería tener 2 vidas tras decremento");
        manager.incrementLives();
        assertEquals(3, manager.getLives(), "Debería tener 3 vidas tras incremento");
    }

    @Test
    void testComboMultiplierFractions() {
        ScoreManager manager = ScoreManager.getInstance();
        Enemy dummy = new SwordsmanEnemy(0, 0);

        // Kill 1 -> Combo 1 -> Multiplicador x1 (Score: 100)
        manager.onEnemyKilled(dummy);
        assertEquals(100, manager.getScore(), "Score debe ser 100 en la primera kill");
        assertEquals(1, manager.getComboCount(), "Combo debe ser 1");

        // Kill 2 -> Combo 2 -> Multiplicador x1 (Score: 200)
        manager.onEnemyKilled(dummy);
        assertEquals(200, manager.getScore(), "Score debe ser 200 en la segunda kill");

        // Kill 3 -> Combo 3 -> Multiplicador x2 (Score: 400 ya que suma 100 * 2 = 200)
        manager.onEnemyKilled(dummy);
        assertEquals(400, manager.getScore(), "Score debe ser 400 en la tercera kill con mult x2");
        assertEquals(3, manager.getComboCount(), "Combo debe ser 3");

        // Kill 4 -> Combo 4 -> Multiplicador x2 (Score: 600 ya que suma 100 * 2 = 200)
        manager.onEnemyKilled(dummy);
        assertEquals(600, manager.getScore(), "Score debe ser 600 en la cuarta kill");
    }
}
