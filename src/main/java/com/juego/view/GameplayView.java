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

import com.juego.system.KeyboardInputDevice;
import com.juego.system.GameInputHandler;
import com.juego.entity.HeroFacade;
import com.juego.entity.Enemy;
import com.juego.entity.EnemyFactory;
import com.juego.entity.BasicEnemyFactory;
import com.juego.entity.EnemyBuilder;
import com.juego.entity.StunnedEnemyDecorator;
import com.juego.weapon.Sword;
import com.juego.weapon.Hammer;
import com.juego.weapon.Bow;
import com.juego.weapon.ArrowPool;
import com.juego.audio.AudioManager;
import com.juego.core.ScoreManager;
import com.juego.entity.EnemyRegistry;
import com.juego.weapon.Arrow;
import java.util.ArrayList;
import java.util.List;

public class GameplayView implements View {
    private Hero hero;
    private DamageSystem damageSystem;
    private EnemyManager enemyManager;
    private CollisionManager collisionManager;
    private AudioSystem audioSystem;
    private JPanel panel;
    
    private KeyboardInputDevice inputDevice;
    private GameInputHandler inputHandler;
    
    private ArrowPool arrowPool;
    private List<Arrow> activeProjectiles;
    private EnemyRegistry enemyRegistry;

    private boolean isRunningGame;

    public GameplayView() {
        this.enemyManager = new EnemyManager(20, 40);
        this.enemyRegistry = () -> enemyManager.getActiveEnemies();
        
        // Inyectar el EnemyRegistry en la espada inicial del héroe
        this.hero = new Hero(100, 300, 100, 4.0f, new Sword(this.enemyRegistry));
        
        this.damageSystem = new DamageSystem();
        this.collisionManager = new CollisionManager(64);
        
        AudioManager audioManager = new AudioManager();
        this.audioSystem = new AudioSystem(audioManager);
        
        // Registrar ScoreManager como observer en el enemyManager
        this.enemyManager.addDeathObserver(ScoreManager.getInstance());
        
        // Inicializar pools y proyectiles
        this.arrowPool = new ArrowPool(20);
        this.activeProjectiles = new ArrayList<>();
        
        // Crear sistema de entrada con patrón Bridge
        this.inputDevice = new KeyboardInputDevice(null);
        this.inputHandler = new GameInputHandler(this.inputDevice);
        
        // Panel canvas premium
        this.panel = new GameCanvas();
        this.panel.setFocusable(true);
        
        this.setupKeyboardInput();
        this.initGame();
    }

    private void setupKeyboardInput() {
        this.panel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                String keyName = null;
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_A:
                    case java.awt.event.KeyEvent.VK_LEFT:
                        keyName = "A";
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        keyName = "D";
                        break;
                    case java.awt.event.KeyEvent.VK_SPACE:
                        keyName = "SPACE";
                        break;
                    case java.awt.event.KeyEvent.VK_SHIFT:
                        keyName = "SHIFT";
                        break;
                    case java.awt.event.KeyEvent.VK_J:
                        keyName = "J";
                        break;
                    // Cambiar armas con teclas numéricas
                    case java.awt.event.KeyEvent.VK_1:
                        hero.changeWeapon(new Sword(enemyRegistry));
                        System.out.println("Weapon switched to SWORD");
                        break;
                    case java.awt.event.KeyEvent.VK_2:
                        hero.changeWeapon(new Hammer(enemyRegistry));
                        System.out.println("Weapon switched to HAMMER");
                        break;
                    case java.awt.event.KeyEvent.VK_3:
                        hero.changeWeapon(new Bow(arrowPool, activeProjectiles));
                        System.out.println("Weapon switched to BOW");
                        break;
                }
                if (keyName != null) {
                    inputDevice.setPressedKey(keyName);
                    // Procesar la entrada a través del Bridge y Facade
                    HeroFacade facade = new HeroFacade(hero);
                    inputHandler.handleInput(facade);
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == java.awt.event.KeyEvent.VK_A || keyCode == java.awt.event.KeyEvent.VK_LEFT ||
                    keyCode == java.awt.event.KeyEvent.VK_D || keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
                    inputDevice.setPressedKey(null);
                    // Detener al héroe en la máquina de estados
                    hero.handleInput("STOP");
                }
            }
        });
    }

    private void initGame() {
        if (this.isRunningGame)
            return;

        this.isRunningGame = true;
        System.out.println("Game started: Game Loop thread active.");

        // Game Loop a 60 FPS
        new Thread(() -> {
            long lastTime = System.nanoTime();
            double nsPerTick = 1000000000.0 / 60.0;
            double delta = 0;
            long lastSpawnTime = System.currentTimeMillis();
            EnemyFactory enemyFactory = new BasicEnemyFactory();

            while (isRunningGame) {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerTick;
                lastTime = now;

                while (delta >= 1) {
                    updateGame(enemyFactory);
                    delta--;
                }

                // Redibuja el canvas Swing
                panel.repaint();

                try {
                    Thread.sleep(10); // Amortiguar carga de CPU
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateGame(EnemyFactory enemyFactory) {
        // 1. Actualizar estado y física del héroe
        hero.updateState();
        
        // Mantener al héroe en los límites de la pantalla (800x600)
        float hx = hero.getAbsoluteX();
        float hy = hero.getAbsoluteY();
        if (hx < 0) hero.move(-hx / hero.getCollider().getWidth(), 0); // Ajuste
        if (hx > 768) hero.move((768 - hx) / hero.getCollider().getWidth(), 0);
        
        // 2. Actualizar sistema de combos
        ScoreManager.getInstance().update();
        
        // 3. Spawner procedural de enemigos cada 4 segundos
        long current = System.currentTimeMillis();
        // Simulamos que aparecen enemigos de forma procedural
        if (enemyManager.getActiveEnemies().size() < 4) {
            float spawnX = (float) (Math.random() * 700) + 50;
            float spawnY = 400.0f; // suelo simulado
            
            String[] types = {"SWORDSMAN", "SHIELDER", "FLYER"};
            String chosenType = types[(int) (Math.random() * types.length)];
            if ("FLYER".equals(chosenType)) {
                spawnY = 200.0f; // altura aire
            }
            
            Enemy newEnemy;
            if (Math.random() < 0.3) {
                // Builder para enemigo de élite
                newEnemy = new EnemyBuilder(chosenType, spawnX, spawnY)
                        .addShield()
                        .addExtraHealth(20)
                        .addExtraSpeed(0.5f)
                        .build();
            } else {
                newEnemy = enemyFactory.createEnemy(chosenType, spawnX, spawnY);
            }
            enemyManager.getActiveEnemies().add(newEnemy);
        }

        // 4. Inteligencia artificial básica para que los enemigos se muevan hacia el héroe
        for (Enemy enemy : enemyManager.getActiveEnemies()) {
            float ex = enemy.getAbsoluteX();
            float ey = enemy.getAbsoluteY();
            float dx = hero.getAbsoluteX() - ex;
            float dy = hero.getAbsoluteY() - ey;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (dist > 10) {
                // Mover al enemigo en dirección al héroe
                enemy.move(dx / dist * 0.1f, dy / dist * 0.1f);
            }
        }

        // 5. Actualizar proyectiles activos y colisiones
        for (int i = activeProjectiles.size() - 1; i >= 0; i--) {
            Arrow arrow = activeProjectiles.get(i);
            arrow.update();
            
            boolean hit = false;
            for (Enemy enemy : enemyManager.getActiveEnemies()) {
                if (arrow.getCollider().checkCollision(enemy.getCollider())) {
                    enemy.reduceHealth(arrow.getDamage());
                    System.out.println("Arrow Collision: Dealt " + arrow.getDamage() + " damage to " + enemy.getFlyweight().getType());
                    hit = true;
                    break;
                }
            }
            
            float ax = arrow.getAbsoluteX();
            float ay = arrow.getAbsoluteY();
            
            if (hit || ax < 0 || ax > 800 || ay < 0 || ay > 600) {
                arrow.deactivate();
                arrowPool.release(arrow);
                activeProjectiles.remove(i);
            }
        }

        // 6. Eliminar enemigos caídos y notificar muerte (Observer)
        for (int i = enemyManager.getActiveEnemies().size() - 1; i >= 0; i--) {
            Enemy enemy = enemyManager.getActiveEnemies().get(i);
            if (enemy.getHealth() <= 0) {
                System.out.println("Enemy defeated: " + enemy.getFlyweight().getType());
                enemyManager.dieEnemy(enemy); // Remueve y dispara Observer
            }
        }
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
        this.isRunningGame = false;
        System.out.println("GameplayView removed. Game loop stopped.");
    }

    public void startGame() {
        GameManager.getInstance().addView(new GameplayView());
        System.out.println("Starting game...");
    }

    public void exitGame() {
        this.isRunningGame = false;
        GameManager.getInstance().removeView(this);
    }

    /**
     * JPanel interno personalizado para renderizado premium AWT/Swing 2D
     */
    private class GameCanvas extends JPanel {
        public GameCanvas() {
            setDoubleBuffered(true);
        }

        @Override
        public void addNotify() {
            super.addNotify();
            requestFocusInWindow();
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            
            // Habilitar antialiasing para gráficos más definidos y estéticos
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Dibujar fondo gradiente premium (noche oscura a negro)
            java.awt.GradientPaint skyGradient = new java.awt.GradientPaint(
                0, 0, new java.awt.Color(12, 14, 36),
                0, getHeight(), new java.awt.Color(5, 5, 10)
            );
            g2.setPaint(skyGradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Dibujar un suelo decorativo elegante
            g2.setColor(new java.awt.Color(34, 40, 49));
            g2.fillRect(0, 432, getWidth(), getHeight() - 432);
            g2.setColor(new java.awt.Color(0, 173, 181));
            g2.fillRect(0, 432, getWidth(), 4); // Línea brillante de borde

            // Dibujar al Héroe
            int hx = (int) hero.getAbsoluteX();
            int hy = (int) hero.getAbsoluteY();
            g2.setColor(new java.awt.Color(0, 173, 181)); // Azul neón brillante
            g2.fillRect(hx, hy, 32, 32);
            
            // Dibujar dirección e indicador visual del arma
            g2.setColor(java.awt.Color.WHITE);
            int dirX = (int) hero.getFacingDirection().x;
            int dirY = (int) hero.getFacingDirection().y;
            g2.drawLine(hx + 16, hy + 16, hx + 16 + dirX * 24, hy + 16 + dirY * 24);

            // Dibujar proyectiles (flechas del arco)
            g2.setColor(new java.awt.Color(255, 235, 59)); // Amarillo brillante
            for (Arrow arrow : activeProjectiles) {
                int ax = (int) arrow.getAbsoluteX();
                int ay = (int) arrow.getAbsoluteY();
                g2.fillOval(ax, ay, 6, 6);
            }

            // Dibujar Enemigos
            for (Enemy enemy : enemyManager.getActiveEnemies()) {
                int ex = (int) enemy.getAbsoluteX();
                int ey = (int) enemy.getAbsoluteY();
                String type = enemy.getFlyweight().getType();

                // Colorear según el tipo (Flyweight)
                if ("Swordsman".equals(type)) {
                    g2.setColor(new java.awt.Color(255, 75, 75)); // Rojo
                } else if ("Shielder".equals(type)) {
                    g2.setColor(new java.awt.Color(255, 165, 0)); // Naranja
                } else if ("Flyer".equals(type)) {
                    g2.setColor(new java.awt.Color(186, 85, 211)); // Púrpura
                } else {
                    g2.setColor(java.awt.Color.GRAY);
                }
                
                // Si el enemigo está aturdido (Decorator)
                boolean isStunned = false;
                if (enemy instanceof StunnedEnemyDecorator) {
                    isStunned = ((StunnedEnemyDecorator) enemy).isStunned();
                }

                if (isStunned) {
                    g2.setColor(new java.awt.Color(128, 128, 128)); // Grisáceo
                }

                // Cuerpo
                g2.fillOval(ex, ey, 24, 24);

                // Dibujar vida restante sobre la cabeza de forma elegante
                g2.setColor(java.awt.Color.RED);
                g2.fillRect(ex - 4, ey - 10, 32, 3);
                g2.setColor(java.awt.Color.GREEN);
                int healthBarWidth = (int) (32 * (enemy.getHealth() / enemy.getFlyweight().getBaseHealth()));
                if (healthBarWidth > 32) healthBarWidth = 32;
                g2.fillRect(ex - 4, ey - 10, healthBarWidth, 3);

                // Dibujar escudo adicional si es Shielder
                if ("Shielder".equals(type) && !isStunned) {
                    g2.setColor(java.awt.Color.WHITE);
                    g2.fillRect(ex - 4, ey, 4, 24);
                }

                // Dibujar un espiral o efecto visual si está aturdido (Decorator activo)
                if (isStunned) {
                    g2.setColor(new java.awt.Color(0, 255, 255));
                    g2.drawArc(ex - 2, ey - 8, 28, 8, 0, 360);
                    g2.setFont(new java.awt.Font("Outfit", java.awt.Font.BOLD, 10));
                    g2.drawString("STUN", ex - 2, ey - 12);
                }
            }

            // --- HUD (Heads-Up Display) ---
            ScoreManager scoreManager = ScoreManager.getInstance();

            // Vidas (Corazones neón)
            g2.setFont(new java.awt.Font("Outfit", java.awt.Font.BOLD, 18));
            g2.setColor(new java.awt.Color(255, 75, 75));
            StringBuilder hearts = new StringBuilder();
            for (int i = 0; i < scoreManager.getLives(); i++) {
                hearts.append("❤️ ");
            }
            g2.drawString("LIVES: " + hearts.toString(), 20, 40);

            // Score
            g2.setColor(java.awt.Color.WHITE);
            g2.drawString("SCORE: " + scoreManager.getScore(), 20, 70);

            // Combo & Multiplicador (Observer)
            int combo = scoreManager.getComboCount();
            if (combo > 0) {
                int mult = (int) Math.pow(2, combo / 3);
                g2.setColor(new java.awt.Color(255, 215, 0)); // Color Dorado
                g2.setFont(new java.awt.Font("Outfit", java.awt.Font.BOLD, 22));
                g2.drawString("COMBO: " + combo + " (x" + mult + ")", 20, 110);
            }

            // Instrucciones del HUD
            g2.setFont(new java.awt.Font("Outfit", java.awt.Font.PLAIN, 12));
            g2.setColor(new java.awt.Color(200, 200, 200));
            g2.drawString("[A/D] Move | [SPACE] Jump | [SHIFT] Dash | [J] Attack", 20, getHeight() - 40);
            g2.drawString("[1] Sword (Fast) | [2] Hammer (Stun) | [3] Bow (8-Way)", 20, getHeight() - 20);
        }
    }
}
