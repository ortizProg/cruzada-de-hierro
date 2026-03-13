package com.juego.view;

import com.juego.core.GameManager;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

public class HomeView implements View {
    private JPanel panel;

    public HomeView() {
        this.panel = new JPanel(new BorderLayout());
        this.render();
    }

    private void render() {
        JLabel title = new JLabel("Cruzada de Hierro", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        panel.add(title, BorderLayout.CENTER);

        JButton startButton = new JButton("Comenzar Juego");
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 24));
        startButton.addActionListener((ActionEvent e) -> startGame());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        System.out.println("UI para HomeView construida y renderizada");
    }

    @Override
    public JPanel getPanel() {
        return this.panel;
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
        System.out.println("Starting game...");
        GameManager.getInstance().addView(new GameplayView());
        GameManager.getInstance().removeView(this);
    }

    public void exitGame() {
        GameManager.getInstance().removeView(this);
    }
}
