package com.juego;

import com.juego.core.GameManager;
import com.juego.view.HomeView;

public class Main {
    public static void main(String[] args) {
        System.out.println("¡Bienvenido al Juego de Patrones!");

        // Aquí comenzaremos a implementar la lógica del juego y los patrones de diseño
        GameManager.getInstance().addView(new HomeView());
    }
}
