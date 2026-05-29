package com.juego.system;

import com.juego.entity.HeroFacade;

/**
 * Command Pattern: Interfaz base para encolar o ejecutar acciones del jugador
 * de forma asíncrona y con baja latencia (RNF2).
 */
public interface ICommand {
    void execute(HeroFacade hero);
}
