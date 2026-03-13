package com.juego.view;

import com.juego.core.GameManager;
import com.juego.entity.Hero;
import com.juego.system.AudioSystem;
import com.juego.system.DamageSystem;
import com.juego.physics.CollisionManager;
import com.juego.entity.EnemyManager;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class GameplayView implements View {
    private Hero hero;
    private DamageSystem damageSystem;
    private EnemyManager enemyManager;
    private CollisionManager collisionManager;
    private AudioSystem audioSystem;
    private JPanel panel;

    private boolean isRunningGame;

    public GameplayView() {
        this.hero = new Hero(0, 0, 0, 0, null);
        this.damageSystem = new DamageSystem();
        this.enemyManager = new EnemyManager(20, 40);
        this.collisionManager = new CollisionManager(64);
        
        this.panel = new JPanel(new BorderLayout());
        this.panel.add(new JLabel("Game is Running...", JLabel.CENTER), BorderLayout.CENTER);
        
        this.initGame();
    }

    private void initGame() {

        // Valida que el juego no se haya iniciado previamente
        if (this.isRunningGame == true)
            return;

        this.isRunningGame = true;

        System.out.println("Game started");

    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public JPanel getPanel() {
        return this.panel;
    }

    @Override
    public void remove() {
        System.out.println("GameplayView removed");
    }

    public void startGame() {
        GameManager.getInstance().addView(new GameplayView());
        System.out.println("Starting game...");
    }

    public void exitGame() {
        GameManager.getInstance().removeView(this);
    }
}
