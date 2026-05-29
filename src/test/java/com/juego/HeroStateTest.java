package com.juego;

import com.juego.entity.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeroStateTest {

    @Test
    void testInitialStateIsIdle() {
        Hero hero = new Hero(100, 100, 100, 4.0f, null);
        assertNotNull(hero.getCurrentState(), "El estado no debe ser nulo");
        assertTrue(hero.getCurrentState() instanceof IdleState, "Debe iniciar en IdleState");
    }

    @Test
    void testTransitionToRunning() {
        Hero hero = new Hero(100, 100, 100, 4.0f, null);
        hero.handleInput("MOVE_RIGHT");
        assertTrue(hero.getCurrentState() instanceof RunningState, "Debe transicionar a RunningState");
    }

    @Test
    void testTransitionToJumpingAndDoubleJump() {
        Hero hero = new Hero(100, 100, 100, 4.0f, null);
        
        // Salto 1
        hero.handleInput("JUMP");
        assertTrue(hero.getCurrentState() instanceof JumpingState, "Debe transicionar a JumpingState");
        assertEquals(1, hero.getJumps(), "Debe registrar 1 salto");

        // Doble Salto en JumpingState
        hero.handleInput("JUMP");
        assertTrue(hero.getCurrentState() instanceof JumpingState, "Debe seguir en JumpingState");
        assertEquals(2, hero.getJumps(), "Debe registrar 2 saltos");

        // Intentar un tercer salto (debería ignorarse)
        hero.handleInput("JUMP");
        assertEquals(2, hero.getJumps(), "No debe permitir más de 2 saltos");
    }

    @Test
    void testTransitionToDashing() {
        Hero hero = new Hero(100, 100, 100, 4.0f, null);
        hero.handleInput("DASH");
        assertTrue(hero.getCurrentState() instanceof DashingState, "Debe transicionar a DashingState");
    }
}
