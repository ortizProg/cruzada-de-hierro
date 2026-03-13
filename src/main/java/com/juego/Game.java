package com.juego;

public class Game {
    private boolean isRunning;

    public Game() {
        this.isRunning = false;
    }

    public void start() {
        this.isRunning = true;
        System.out.println("Inicializando configuración del juego...");
        
        // Simulación de un bucle de juego básico
        while (isRunning) {
            update();
            render();
            
            // Para propósitos de este ejemplo inicial, detendremos el juego inmediatamente
            stop();
        }
    }

    private void update() {
        System.out.println("Actualizando lógica del juego...");
    }

    private void render() {
        System.out.println("Renderizando gráficos...");
    }

    public void stop() {
        this.isRunning = false;
        System.out.println("Juego detenido.");
    }
}
