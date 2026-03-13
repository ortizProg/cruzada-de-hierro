package com.juego.core;

import com.juego.view.View;
import com.juego.view.GameWindow;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton Pattern: GameManager orquesta el bucle de juego y administra la
 * pila de vistas.
 * Es la única clase que puede ser Singleton según las reglas.
 */
public class GameManager {
    private static GameManager instance;

    private List<View> currentViews;
    private List<Process> processQueue;
    private GameWindow window;

    private GameManager() {
        currentViews = new ArrayList<>();
        processQueue = new ArrayList<>();
        window = new GameWindow("Cruzada de Hierro", 800, 600);
        window.setVisible(true);
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void addView(View view) {
        currentViews.add(view);
        window.getContentPane().removeAll();
        window.add(view.getPanel());
        window.revalidate();
        window.repaint();
    }

    public void removeView(View view) {
        currentViews.remove(view);
        view.remove();
    }

    public void addProcess(Process process) {
        processQueue.add(process);
    }

    public void removeProcess(Process process) {
        processQueue.remove(process);
    }

    public List<View> getCurrentViews() {
        return currentViews;
    }

    public GameWindow getWindow() {
        return window;
    }
}
