package com.juego.system;

import com.juego.entity.HeroFacade;
import com.juego.math.Vector2;

/**
 * Command concreto para ejecutar el Ataque en el HeroFacade.
 * Soporta disparos dirigidos en 8 direcciones.
 */
public class AttackCommand implements ICommand {
    private Vector2 direction;

    public AttackCommand() {
        this.direction = null; // Usa dirección por defecto del Hero
    }

    public AttackCommand(Vector2 direction) {
        this.direction = direction; // Dirección 8-way específica
    }

    @Override
    public void execute(HeroFacade hero) {
        if (direction != null) {
            System.out.println("Command Executed: ATTACK at target direction (" + direction.x + ", " + direction.y + ")");
            hero.attackDirection(direction);
        } else {
            System.out.println("Command Executed: ATTACK");
            hero.attack();
        }
    }
}
