package com.juego.weapon;

/**
 * Strategy Pattern: Permite equipar diferentes armas al Hero 
 * e invocar attack() polimórficamente sin usar if/else ni switch.
 */
public interface IWeapon {
    void attack();
}
