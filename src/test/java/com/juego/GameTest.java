package com.juego;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testGameInitialization() {
        Game game = new Game();
        assertNotNull(game, "El juego debería inicializarse correctamente");
    }
}
