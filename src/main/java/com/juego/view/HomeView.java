package com.juego.view;

import com.juego.core.GameManager;

public class HomeView implements View {

    public HomeView() {
        System.out.println("HomeView created");
    }

    private void render() {
        System.out.println("HomeView rendered");
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void remove() {
        System.out.println("HomeView removed");
    }

    public void startGame() {
        GameManager.getInstance().addView(new GameplayView());
        System.out.println("Starting game...");
    }

    public void exitGame() {
        GameManager.getInstance().removeView(this);
    }
}
