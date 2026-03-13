package com.juego.view;

import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * GameWindow encapsulates the main JFrame for the application.
 */
public class GameWindow extends JFrame {
    
    public GameWindow(String title, int width, int height) {
        setTitle(title);
        setSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
    }
}
