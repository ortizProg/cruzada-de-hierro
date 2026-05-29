package com.juego.weapon;

import com.juego.math.Vector2;

/**
 * Strategy Pattern: Permite equipar diferentes armas al Hero 
 * e invocar attack() polimórficamente sin usar if/else ni switch.
 */
public interface IWeapon {
    void attack(Vector2 position, Vector2 direction);
}

