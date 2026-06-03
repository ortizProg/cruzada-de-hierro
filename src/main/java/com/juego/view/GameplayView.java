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
import com.juego.physics.LevelBlock;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

import com.juego.system.AssetManager;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.BasicStroke;

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

    // Campos añadidos para el scroll y generación automática del mapa
    private List<LevelBlock> levelBlocks;
    private final int LEVEL_WIDTH = 4800;
    private int cameraX = 0;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private long lastDamageTime = 0;
    private final long INVULNERABILITY_COOLDOWN_MS = 1500;

    private int mouseX = 0;
    private int mouseY = 0;

    private boolean checkCollision(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    public GameplayView() {
        this.enemyManager = new EnemyManager(20, 40);
        this.enemyRegistry = () -> enemyManager.getActiveEnemies();
        
        // Inyectar el EnemyRegistry en la espada inicial del héroe
        this.hero = new Hero(100, 200, 100, 4.0f, new Sword(this.enemyRegistry));
        
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
        
        // Generar nivel procedimental alternando Castillo y Campo Abierto
        generateLevel();
        
        // Panel canvas premium
        this.panel = new GameCanvas();
        this.panel.setFocusable(true);
        
        this.setupKeyboardInput();
        this.setupMouseInput();
        
        // Reiniciar puntuaciones y vidas globales
        ScoreManager.getInstance().reset();
        
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
                        leftPressed = true;
                        keyName = "A";
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        rightPressed = true;
                        keyName = "D";
                        break;
                    case java.awt.event.KeyEvent.VK_W:
                    case java.awt.event.KeyEvent.VK_UP:
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
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_A:
                    case java.awt.event.KeyEvent.VK_LEFT:
                        leftPressed = false;
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        rightPressed = false;
                        break;
                }
                if (!leftPressed && !rightPressed) {
                    inputDevice.setPressedKey(null);
                    // Detener al héroe en la máquina de estados
                    hero.handleInput("STOP");
                }
            }
        });
    }

    private void setupMouseInput() {
        java.awt.event.MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Click izquierdo
                    mouseX = e.getX();
                    mouseY = e.getY();
                    
                    // Actualizar dirección inmediatamente antes del ataque
                    hero.setFacingDirection(getMouseDirection());
                    
                    // Ejecutar ataque
                    HeroFacade facade = new HeroFacade(hero);
                    inputDevice.setPressedKey("J");
                    inputHandler.handleInput(facade);
                }
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
        this.panel.addMouseListener(mouseAdapter);
        this.panel.addMouseMotionListener(mouseAdapter);
    }

    private com.juego.math.Vector2 getMouseDirection() {
        float worldMouseX = mouseX + cameraX;
        float worldMouseY = mouseY;
        
        float hCenterX = hero.getAbsoluteX() + 16;
        float hCenterY = hero.getAbsoluteY() + 16;
        
        float dx = worldMouseX - hCenterX;
        float dy = worldMouseY - hCenterY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            return new com.juego.math.Vector2(dx / dist, dy / dist);
        } else {
            return new com.juego.math.Vector2(1, 0);
        }
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
        // Reset/init ScoreManager if needed (lives, combo update)
        ScoreManager.getInstance().update();

        // Guardar la posición previa antes de actualizar
        float prevX = hero.getAbsoluteX();
        float prevY = hero.getAbsoluteY();

        // 1. Actualizar estado y aplicar gravedad si no está haciendo dash
        hero.updateState();
        if (!(hero.getCurrentState() instanceof com.juego.entity.DashingState)) {
            hero.setVy(hero.getVy() + 0.6f); // Gravedad
        } else {
            hero.setVy(0);
        }

        // 2. Procesar movimiento horizontal continuo por teclas
        if (leftPressed) {
            inputDevice.setPressedKey("A");
            HeroFacade facade = new HeroFacade(hero);
            inputHandler.handleInput(facade);
        } else if (rightPressed) {
            inputDevice.setPressedKey("D");
            HeroFacade facade = new HeroFacade(hero);
            inputHandler.handleInput(facade);
        }

        // Resolver colisión horizontal
        for (LevelBlock block : levelBlocks) {
            if (block.getType().equals("GROUND")) {
                if (checkCollision(hero.getAbsoluteX(), hero.getAbsoluteY(), hero.getCollider().getWidth(), hero.getCollider().getHeight(),
                                   block.getAbsoluteX(), block.getAbsoluteY(), block.getWidth(), block.getHeight())) {
                    hero.setPosition(prevX, hero.getAbsoluteY());
                    break;
                }
            }
        }

        // Aplicar movimiento vertical
        hero.setPosition(hero.getAbsoluteX(), hero.getAbsoluteY() + hero.getVy());

        // Resolver colisión vertical
        boolean hitGround = false;
        for (LevelBlock block : levelBlocks) {
            if (block.getType().equals("GROUND") || block.getType().equals("PLATFORM")) {
                boolean canCollide = true;
                if (block.getType().equals("PLATFORM")) {
                    canCollide = (hero.getVy() > 0) && (prevY + hero.getCollider().getHeight() <= block.getAbsoluteY() + 4);
                }

                if (canCollide) {
                    if (checkCollision(hero.getAbsoluteX(), hero.getAbsoluteY(), hero.getCollider().getWidth(), hero.getCollider().getHeight(),
                                       block.getAbsoluteX(), block.getAbsoluteY(), block.getWidth(), block.getHeight())) {
                        if (hero.getVy() > 0) { // cayendo
                            hero.setPosition(hero.getAbsoluteX(), block.getAbsoluteY() - hero.getCollider().getHeight());
                            hero.setVy(0);
                            hero.setOnGround(true);
                            hero.resetJumps();
                            hitGround = true;
                            if (hero.getCurrentState() instanceof com.juego.entity.JumpingState) {
                                hero.handleInput("LAND");
                            }
                        } else if (hero.getVy() < 0 && block.getType().equals("GROUND")) { // subiendo y choca con techo
                            hero.setPosition(hero.getAbsoluteX(), block.getAbsoluteY() + block.getHeight());
                            hero.setVy(0);
                        }
                    }
                }
            }
        }

        if (!hitGround) {
            hero.setOnGround(false);
            if (!(hero.getCurrentState() instanceof com.juego.entity.JumpingState) && !(hero.getCurrentState() instanceof com.juego.entity.DashingState)) {
                hero.changeState(new com.juego.entity.JumpingState());
            }
        }

        // Actualizar dirección de apuntado con el mouse (si no está haciendo dash)
        if (!(hero.getCurrentState() instanceof com.juego.entity.DashingState)) {
            hero.setFacingDirection(getMouseDirection());
        }

        // Verificar colisión con SPIKES (Pinchos)
        long currentTime = System.currentTimeMillis();
        boolean isHeroInvulnerable = (currentTime - lastDamageTime < INVULNERABILITY_COOLDOWN_MS) || (hero.getCurrentState() instanceof com.juego.entity.DashingState);
        
        if (!isHeroInvulnerable) {
            for (LevelBlock block : levelBlocks) {
                if (block.getType().equals("SPIKES")) {
                    if (checkCollision(hero.getAbsoluteX(), hero.getAbsoluteY(), hero.getCollider().getWidth(), hero.getCollider().getHeight(),
                                       block.getAbsoluteX(), block.getAbsoluteY(), block.getWidth(), block.getHeight())) {
                        ScoreManager.getInstance().decrementLives();
                        lastDamageTime = currentTime;
                        hero.setVy(-5.0f);
                        float knockDir = (hero.getFacingDirection().x < 0) ? 1.0f : -1.0f;
                        hero.move(knockDir * 3, 0);
                        System.out.println("Hero touched spikes! Lives: " + ScoreManager.getInstance().getLives());
                        
                        if (ScoreManager.getInstance().getLives() <= 0) {
                            JOptionPane.showMessageDialog(panel, "¡HAS CAÍDO EN LA CRUZADA! Game Over", "Fin de la Partida", JOptionPane.ERROR_MESSAGE);
                            exitGame();
                            return;
                        } else {
                            // Respawn en lugar seguro de este segmento
                            int currentSegment = (int) (hero.getAbsoluteX() / 800);
                            hero.setPosition(currentSegment * 800 + 100, 200);
                            hero.setVy(0);
                            hero.setOnGround(false);
                            hero.changeState(new com.juego.entity.JumpingState());
                        }
                        break;
                    }
                }
            }
        }

        // Verificar colisión con PORTAL (Victoria)
        for (LevelBlock block : levelBlocks) {
            if (block.getType().equals("PORTAL")) {
                if (checkCollision(hero.getAbsoluteX(), hero.getAbsoluteY(), hero.getCollider().getWidth(), hero.getCollider().getHeight(),
                                   block.getAbsoluteX(), block.getAbsoluteY(), block.getWidth(), block.getHeight())) {
                    JOptionPane.showMessageDialog(panel, "¡VICTORIA! Sir Kaelen ha purgado la plaga del Reino.", "Cruzada de Hierro", JOptionPane.INFORMATION_MESSAGE);
                    exitGame();
                    return;
                }
            }
        }

        // Verificar caída al vacío (Abismo)
        if (hero.getAbsoluteY() > 600) {
            ScoreManager.getInstance().decrementLives();
            lastDamageTime = currentTime;
            System.out.println("Hero fell in abyss! Lives: " + ScoreManager.getInstance().getLives());
            
            if (ScoreManager.getInstance().getLives() <= 0) {
                JOptionPane.showMessageDialog(panel, "¡HAS CAÍDO EN LA CRUZADA! Game Over", "Fin de la Partida", JOptionPane.ERROR_MESSAGE);
                exitGame();
                return;
            } else {
                int currentSegment = (int) (hero.getAbsoluteX() / 800);
                hero.setPosition(currentSegment * 800 + 100, 200);
                hero.setVy(0);
                hero.setOnGround(false);
                hero.changeState(new com.juego.entity.JumpingState());
            }
        }

        // 3. Spawner procedural de enemigos off-screen cada 4 segundos
        if (enemyManager.getActiveEnemies().size() < 6 && Math.random() < 0.015) {
            float spawnX = cameraX + 850;
            if (spawnX < LEVEL_WIDTH) {
                float spawnY = 400.0f;
                String[] types = {"SWORDSMAN", "SHIELDER", "FLYER"};
                String chosenType = types[(int) (Math.random() * types.length)];
                
                if ("FLYER".equals(chosenType)) {
                    spawnY = 150.0f + (float)(Math.random() * 150);
                } else {
                    for (LevelBlock block : levelBlocks) {
                        if (block.getType().equals("GROUND") && spawnX >= block.getAbsoluteX() && spawnX <= block.getAbsoluteX() + block.getWidth()) {
                            spawnY = block.getAbsoluteY() - 32;
                            break;
                        }
                    }
                }

                Enemy newEnemy;
                if (Math.random() < 0.25) {
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
        }

        // 4. Física e IA de enemigos
        for (Enemy enemy : enemyManager.getActiveEnemies()) {
            boolean isStunned = false;
            if (enemy instanceof StunnedEnemyDecorator) {
                isStunned = ((StunnedEnemyDecorator) enemy).isStunned();
            }

            if (isStunned) {
                continue; // Aturdido no se mueve
            }

            float ex = enemy.getAbsoluteX();
            float ey = enemy.getAbsoluteY();
            float dx = hero.getAbsoluteX() - ex;
            float dy = hero.getAbsoluteY() - ey;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            
            String eType = enemy.getFlyweight().getType();

            if ("Flyer".equalsIgnoreCase(eType)) {
                // Vuela hacia el héroe libremente
                if (dist > 10) {
                    enemy.move(dx / dist * 0.2f, dy / dist * 0.2f);
                }
            } else {
                // Enemigos terrestres experimentan gravedad y caminan
                enemy.setVy(enemy.getVy() + 0.6f);
                float ePrevX = enemy.getAbsoluteX();
                float ePrevY = enemy.getAbsoluteY();

                float walkDir = (dx > 0) ? 0.22f : -0.22f;
                enemy.move(walkDir, 0);

                // Colisión horizontal del enemigo
                for (LevelBlock block : levelBlocks) {
                    if (block.getType().equals("GROUND")) {
                        if (checkCollision(enemy.getAbsoluteX(), enemy.getAbsoluteY(), enemy.getCollider().getWidth(), enemy.getCollider().getHeight(),
                                           block.getAbsoluteX(), block.getAbsoluteY(), block.getWidth(), block.getHeight())) {
                            enemy.setPosition(ePrevX, enemy.getAbsoluteY());
                            break;
                        }
                    }
                }

                // Movimiento vertical
                enemy.setPosition(enemy.getAbsoluteX(), enemy.getAbsoluteY() + enemy.getVy());

                // Colisión vertical del enemigo
                boolean eHitGround = false;
                for (LevelBlock block : levelBlocks) {
                    if (block.getType().equals("GROUND") || block.getType().equals("PLATFORM")) {
                        boolean canCollide = true;
                        if (block.getType().equals("PLATFORM")) {
                            canCollide = (enemy.getVy() > 0) && (ePrevY + enemy.getCollider().getHeight() <= block.getAbsoluteY() + 4);
                        }

                        if (canCollide) {
                            if (checkCollision(enemy.getAbsoluteX(), enemy.getAbsoluteY(), enemy.getCollider().getWidth(), enemy.getCollider().getHeight(),
                                               block.getAbsoluteX(), block.getAbsoluteY(), block.getWidth(), block.getHeight())) {
                                if (enemy.getVy() > 0) {
                                    enemy.setPosition(enemy.getAbsoluteX(), block.getAbsoluteY() - enemy.getCollider().getHeight());
                                    enemy.setVy(0);
                                    enemy.setOnGround(true);
                                    eHitGround = true;
                                }
                            }
                        }
                    }
                }
                if (!eHitGround) {
                    enemy.setOnGround(false);
                }
                
                // Si caminan fuera del abismo de la pantalla, los eliminamos
                if (enemy.getAbsoluteY() > 620) {
                    enemy.reduceHealth(999);
                }
            }
        }

        // Colisión de héroe con enemigos (i-frames aplicados)
        if (!isHeroInvulnerable) {
            for (Enemy enemy : enemyManager.getActiveEnemies()) {
                if (hero.getCollider().checkCollision(enemy.getCollider())) {
                    ScoreManager.getInstance().decrementLives();
                    lastDamageTime = currentTime;
                    hero.setVy(-4.5f);
                    float knockDir = (hero.getAbsoluteX() < enemy.getAbsoluteX()) ? -1.2f : 1.2f;
                    hero.move(knockDir * 3, 0);
                    System.out.println("Hero damaged by enemy! Lives: " + ScoreManager.getInstance().getLives());
                    
                    if (ScoreManager.getInstance().getLives() <= 0) {
                        JOptionPane.showMessageDialog(panel, "¡HAS CAÍDO EN LA CRUZADA! Game Over", "Fin de la Partida", JOptionPane.ERROR_MESSAGE);
                        exitGame();
                        return;
                    }
                    break;
                }
            }
        }

        // 5. Actualizar proyectiles activos
        for (int i = activeProjectiles.size() - 1; i >= 0; i--) {
            Arrow arrow = activeProjectiles.get(i);
            arrow.update();

            boolean hit = false;
            for (Enemy enemy : enemyManager.getActiveEnemies()) {
                if (arrow.getCollider().checkCollision(enemy.getCollider())) {
                    enemy.reduceHealth(arrow.getDamage());
                    System.out.println("Arrow Hit: " + enemy.getFlyweight().getType() + " for " + arrow.getDamage() + " damage.");
                    hit = true;
                    break;
                }
            }

            float ax = arrow.getAbsoluteX();
            float ay = arrow.getAbsoluteY();

            if (hit || ax < cameraX - 50 || ax > cameraX + 850 || ay < -100 || ay > 700) {
                arrow.deactivate();
                arrowPool.release(arrow);
                activeProjectiles.remove(i);
            }
        }

        // 6. Eliminar enemigos caídos
        for (int i = enemyManager.getActiveEnemies().size() - 1; i >= 0; i--) {
            Enemy enemy = enemyManager.getActiveEnemies().get(i);
            if (enemy.getHealth() <= 0) {
                enemyManager.dieEnemy(enemy);
            }
        }

        // 7. Scroll de cámara
        cameraX = (int) (hero.getAbsoluteX() - 350);
        if (cameraX < 0) cameraX = 0;
        if (cameraX > LEVEL_WIDTH - 800) cameraX = LEVEL_WIDTH - 800;
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

    private void generateLevel() {
        this.levelBlocks = new ArrayList<>();
        int segmentWidth = 800;
        
        // Generar 6 segmentos alternando estilos
        for (int seg = 0; seg < 6; seg++) {
            String style = (seg % 2 == 0) ? "FIELD" : "CASTLE";
            int startX = seg * segmentWidth;
            
            // Suelo base del segmento con algunos abismos o variaciones
            if (seg == 0) {
                // Suelo continuo para empezar seguro
                levelBlocks.add(new LevelBlock(startX, 432, 800, 168, "GROUND", style));
            } else if (seg == 1) {
                // Castillo: Suelo con foso de pinchos en medio
                levelBlocks.add(new LevelBlock(startX, 432, 350, 168, "GROUND", style));
                // Pinchos en el foso
                levelBlocks.add(new LevelBlock(startX + 350, 520, 150, 48, "SPIKES", style));
                levelBlocks.add(new LevelBlock(startX + 500, 432, 300, 168, "GROUND", style));
                
                // Plataformas flotantes sobre el foso para poder saltar
                levelBlocks.add(new LevelBlock(startX + 300, 310, 100, 20, "PLATFORM", style));
                levelBlocks.add(new LevelBlock(startX + 430, 310, 100, 20, "PLATFORM", style));
            } else if (seg == 2) {
                // Campo abierto: abismo de caída al vacío
                levelBlocks.add(new LevelBlock(startX, 432, 300, 168, "GROUND", style));
                // Abismo de 200px sin bloques de suelo!
                levelBlocks.add(new LevelBlock(startX + 500, 432, 300, 168, "GROUND", style));
                
                // Plataforma flotante en el abismo
                levelBlocks.add(new LevelBlock(startX + 340, 300, 120, 20, "PLATFORM", style));
                levelBlocks.add(new LevelBlock(startX + 200, 220, 100, 20, "PLATFORM", style));
            } else if (seg == 3) {
                // Castillo: varios pilares y plataformas elevadas
                levelBlocks.add(new LevelBlock(startX, 432, 200, 168, "GROUND", style));
                levelBlocks.add(new LevelBlock(startX + 300, 432, 120, 168, "GROUND", style));
                levelBlocks.add(new LevelBlock(startX + 500, 432, 300, 168, "GROUND", style));
                
                // Plataformas escalonadas
                levelBlocks.add(new LevelBlock(startX + 180, 320, 100, 20, "PLATFORM", style));
                levelBlocks.add(new LevelBlock(startX + 400, 240, 100, 20, "PLATFORM", style));
            } else if (seg == 4) {
                // Campo abierto: terreno irregular
                levelBlocks.add(new LevelBlock(startX, 432, 400, 168, "GROUND", style));
                levelBlocks.add(new LevelBlock(startX + 480, 380, 320, 220, "GROUND", style)); // Suelo más alto
                
                levelBlocks.add(new LevelBlock(startX + 350, 280, 100, 20, "PLATFORM", style));
            } else {
                // Castillo final
                levelBlocks.add(new LevelBlock(startX, 432, 800, 168, "GROUND", style));
                // Portal de victoria final en el extremo
                levelBlocks.add(new LevelBlock(startX + 650, 332, 80, 100, "PORTAL", style));
            }
        }
    }

    public void startGame() {
        GameManager.getInstance().addView(new GameplayView());
        System.out.println("Starting game...");
    }

    public void exitGame() {
        this.isRunningGame = false;
        GameManager.getInstance().removeView(this);
        // Regresa a HomeView
        GameManager.getInstance().addView(new HomeView());
    }

    private class GameCanvas extends JPanel {
        private double time = 0;

        public GameCanvas() {
            setDoubleBuffered(true);
            new javax.swing.Timer(16, e -> {
                time += 0.5;
                repaint();
            }).start();
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
            
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            long currentTime = System.currentTimeMillis();

            // ====================================================
            // 1. CIELO DINÁMICO GRADIENTE
            // ====================================================
            float playerProgress = hero.getAbsoluteX() / LEVEL_WIDTH;
            Color skyTop = mixColors(new Color(110, 190, 240), new Color(40, 35, 60), playerProgress);
            Color skyBottom = mixColors(new Color(250, 240, 210), new Color(20, 15, 30), playerProgress);
            
            java.awt.GradientPaint skyGradient = new java.awt.GradientPaint(
                0, 0, skyTop,
                0, getHeight(), skyBottom
            );
            g2.setPaint(skyGradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Sol / Luna de fondo
            g2.setColor(new Color(255, 230, 160, 40));
            g2.fillOval(600 - (int)(cameraX * 0.05f), 70, 120, 120);
            g2.setColor(new Color(255, 240, 200, 180));
            g2.fillOval(630 - (int)(cameraX * 0.05f), 100, 60, 60);

            // ====================================================
            // 2. PARALLAX LAYER 1 (Fondo lejano - factor 0.15)
            // ====================================================
            int px1 = - (int) (cameraX * 0.15f);
            for (int seg = 0; seg < 6; seg++) {
                int segX = seg * 800 + px1;
                if (seg % 2 == 0) { // FIELD
                    g2.setColor(new Color(110, 160, 190, 140)); // Montaña lejana
                    int[] mx = { segX - 150, segX + 300, segX + 750, segX + 750, segX - 150 };
                    int[] my = { 432, 230, 432, 600, 600 };
                    g2.fillPolygon(mx, my, 5);
                } else { // CASTLE
                    g2.setColor(new Color(85, 90, 105, 160)); // Torre de castillo lejana
                    g2.fillRect(segX + 150, 150, 140, 300);
                    g2.fillRect(segX + 110, 90, 40, 360);
                    g2.fillRect(segX + 290, 90, 40, 360);
                    int[] rx1 = { segX + 110, segX + 130, segX + 150 };
                    int[] ry1 = { 90, 60, 90 };
                    g2.fillPolygon(rx1, ry1, 3);
                    int[] rx2 = { segX + 290, segX + 310, segX + 330 };
                    int[] ry2 = { 90, 60, 90 };
                    g2.fillPolygon(rx2, ry2, 3);
                }
            }

            // ====================================================
            // 3. PARALLAX LAYER 2 (Fondo medio - factor 0.40)
            // ====================================================
            int px2 = - (int) (cameraX * 0.40f);
            for (int seg = 0; seg < 6; seg++) {
                int segX = seg * 800 + px2;
                if (seg % 2 == 0) { // FIELD
                    g2.setColor(new Color(40, 110, 70, 190)); // Colinas boscosas
                    g2.fillOval(segX - 50, 350, 450, 200);
                    g2.fillOval(segX + 250, 370, 550, 200);
                    // Pinos
                    g2.setColor(new Color(25, 80, 50));
                    for (int tx = segX + 60; tx < segX + 750; tx += 200) {
                        int[] xPts = { tx, tx - 12, tx + 12 };
                        int[] yPts = { 330, 380, 380 };
                        g2.fillPolygon(xPts, yPts, 3);
                        g2.fillRect(tx - 3, 380, 6, 15);
                    }
                } else { // CASTLE
                    g2.setColor(new Color(55, 60, 70, 200)); // Murallas
                    g2.fillRect(segX + 180, 260, 40, 200);
                    g2.fillRect(segX + 360, 260, 40, 200);
                    g2.fillRect(segX + 180, 260, 220, 30);
                    g2.fillArc(segX + 220, 275, 140, 140, 0, 180);
                }
            }

            // ====================================================
            // 4. CAPA DE JUEGO (Mundo principal desplazado por cameraX)
            // ====================================================
            g2.translate(-cameraX, 0);

            // A. Dibujar bloques de nivel
            for (LevelBlock block : levelBlocks) {
                int bx = (int) block.getAbsoluteX();
                int by = (int) block.getAbsoluteY();
                int bw = (int) block.getWidth();
                int bh = (int) block.getHeight();
                
                BufferedImage tileImg = AssetManager.getInstance().getTileSprite(block.getType(), block.getStyle());
                if (tileImg != null) {
                    int tw = tileImg.getWidth();
                    int th = tileImg.getHeight();
                    // Tiling (repetir imagen en el área)
                    for (int tx = bx; tx < bx + bw; tx += tw) {
                        for (int ty = by; ty < by + bh; ty += th) {
                            int drawW = Math.min(tw, bx + bw - tx);
                            int drawH = Math.min(th, by + bh - ty);
                            g2.drawImage(tileImg, tx, ty, tx + drawW, ty + drawH, 0, 0, drawW, drawH, null);
                        }
                    }
                } else {
                    if (block.getType().equals("GROUND")) {
                        if (block.getStyle().equals("FIELD")) {
                            // Tierra
                            g2.setColor(new Color(112, 84, 54));
                            g2.fillRect(bx, by, bw, bh);
                            // Borde de hierba
                            g2.setColor(new Color(34, 139, 34));
                            g2.fillRect(bx, by, bw, 8);
                            // Borde brillante
                            g2.setColor(new Color(144, 238, 144));
                            g2.fillRect(bx, by, bw, 2);
                            // Detalles de briznas
                            g2.setColor(new Color(34, 139, 34));
                            for (int gx = bx + 15; gx < bx + bw; gx += 45) {
                                g2.drawLine(gx, by, gx - 2, by - 4);
                                g2.drawLine(gx, by, gx + 2, by - 4);
                            }
                        } else { // CASTLE
                            // Piedra castillo
                            g2.setColor(new Color(60, 64, 76));
                            g2.fillRect(bx, by, bw, bh);
                            g2.setColor(new Color(85, 90, 105));
                            g2.fillRect(bx, by, bw, 8);
                            // Grout lines
                            g2.setColor(new Color(35, 35, 45));
                            g2.setStroke(new BasicStroke(1.5f));
                            for (int gx = bx; gx < bx + bw; gx += 50) {
                                g2.drawLine(gx, by, gx, by + bh);
                            }
                            for (int gy = by + 25; gy < by + bh; gy += 25) {
                                g2.drawLine(bx, gy, bx + bw, gy);
                            }
                        }
                    } else if (block.getType().equals("PLATFORM")) {
                        if (block.getStyle().equals("FIELD")) {
                            g2.setColor(new Color(139, 90, 43));
                            g2.fillRoundRect(bx, by, bw, bh, 6, 6);
                            g2.setColor(new Color(80, 50, 25));
                            g2.setStroke(new BasicStroke(2.0f));
                            g2.drawRoundRect(bx, by, bw, bh, 6, 6);
                        } else { // CASTLE
                            g2.setColor(new Color(95, 100, 115));
                            g2.fillRoundRect(bx, by, bw, bh, 4, 4);
                            g2.setColor(new Color(45, 45, 55));
                            g2.setStroke(new BasicStroke(2.0f));
                            g2.drawRoundRect(bx, by, bw, bh, 4, 4);
                            // Runas neón brillantes
                            g2.setColor(new Color(0, 173, 181, 140));
                            g2.drawString("✴ ✴ ✴", bx + bw/2 - 20, by + 14);
                        }
                    } else if (block.getType().equals("SPIKES")) {
                        g2.setColor(new Color(160, 160, 175));
                        for (int sx = bx; sx < bx + bw; sx += 16) {
                            int[] xPts = { sx, sx + 8, sx + 16 };
                            int[] yPts = { by + bh, by, by + bh };
                            g2.fillPolygon(xPts, yPts, 3);
                            // Sangre decorativa en puntas
                            g2.setColor(new Color(175, 20, 20));
                            int[] xT = { sx + 5, sx + 8, sx + 11 };
                            int[] yT = { by + 8, by, by + 8 };
                            g2.fillPolygon(xT, yT, 3);
                            g2.setColor(new Color(160, 160, 175));
                        }
                    } else if (block.getType().equals("PORTAL")) {
                        double rot = time * 0.08;
                        g2.setColor(new Color(254, 110, 0, 40));
                        g2.fillOval(bx - 15, by - 15, bw + 30, bh + 30);
                        // Espirales
                        g2.setColor(new Color(255, 200, 30, 200));
                        g2.setStroke(new BasicStroke(3.0f));
                        g2.drawArc(bx, by, bw, bh, (int)(rot * 180 / Math.PI), 240);
                        g2.drawArc(bx + 12, by + 12, bw - 24, bh - 24, (int)(-rot * 180 / Math.PI), 240);
                        // Centro oscuro
                        g2.setColor(new Color(15, 10, 25));
                        g2.fillOval(bx + 20, by + 20, bw - 40, bh - 40);
                    }
                }
            }

            // B. Dibujar Proyectiles (Flechas)
            g2.setStroke(new BasicStroke(2.0f));
            BufferedImage arrowImg = AssetManager.getInstance().getSprite("/assets/projectile_arrow.png");
            for (Arrow arrow : activeProjectiles) {
                int ax = (int) arrow.getAbsoluteX();
                int ay = (int) arrow.getAbsoluteY();
                
                double angle = Math.atan2(arrow.getDirY(), arrow.getDirX());
                
                if (arrowImg != null) {
                    double imgAngle = angle + Math.PI / 2; // Offset de 90 grados porque el sprite original es vertical (apunta hacia arriba)
                    g2.rotate(imgAngle, ax, ay);
                    g2.drawImage(arrowImg, ax - arrowImg.getWidth() / 2, ay - arrowImg.getHeight() / 2, null);
                    g2.rotate(-imgAngle, ax, ay);
                } else {
                    g2.rotate(angle, ax, ay);
                    g2.setColor(new Color(255, 215, 0)); // Flecha dorada
                    g2.fillOval(ax - 3, ay - 3, 6, 6);
                    
                    // Estela brillante (opuesta a la dirección de movimiento)
                    g2.setColor(new Color(255, 165, 0, 100));
                    g2.drawLine(ax, ay, ax - 10, ay);
                    g2.rotate(-angle, ax, ay);
                }
            }

            // C. Dibujar Héroe (Sir Kaelen)
            int hx = (int) hero.getAbsoluteX();
            int hy = (int) hero.getAbsoluteY();
            boolean isFacingLeft = hero.getFacingDirection().x < 0;
            boolean isInvuln = (currentTime - lastDamageTime < INVULNERABILITY_COOLDOWN_MS);

            com.juego.weapon.IWeapon currentW = hero.getEquippedWeapon();
            String weaponType = currentW.getClass().getSimpleName();

            if (!(isInvuln && (currentTime / 120 % 2 == 0))) {
                // Determinar estado para el sprite
                String stateName = "idle";
                if (hero.getCurrentState() instanceof com.juego.entity.RunningState) {
                    stateName = "run";
                } else if (hero.getCurrentState() instanceof com.juego.entity.JumpingState) {
                    stateName = "jump";
                } else if (hero.getCurrentState() instanceof com.juego.entity.DashingState) {
                    stateName = "dash";
                } else if (hero.getCurrentState() instanceof com.juego.entity.AttackingState) {
                    stateName = "attack";
                }

                BufferedImage heroImg = AssetManager.getInstance().getHeroSprite(stateName, isFacingLeft, time, weaponType);
                if (heroImg != null) {
                    int drawW = 64; // Escalar el frame de 256x256 a 64x64 para mejor calidad
                    int drawH = 64;
                    int drawX = hx + (32 - drawW) / 2;
                    int drawY = hy + 32 - drawH; // Alinear base
                    g2.drawImage(heroImg, drawX, drawY, drawW, drawH, null);
                } else {
                    // Capa roja ondulante
                    g2.setColor(new Color(185, 28, 28));
                    int cpOff = isFacingLeft ? 14 : -14;
                    int wave = (int) (Math.sin(time * 0.22) * 5);
                    int[] cpX = { hx + 16, hx + 16 + cpOff, hx + 16 + cpOff + wave, hx + 16 };
                    int[] cpY = { hy + 10, hy + 26, hy + 30, hy + 26 };
                    g2.fillPolygon(cpX, cpY, 4);

                    // Armadura
                    g2.setColor(new Color(110, 115, 125)); // Armadura plateada
                    g2.fillRoundRect(hx + 4, hy + 8, 24, 20, 8, 8);
                    g2.setColor(new Color(70, 75, 85));
                    g2.drawRoundRect(hx + 4, hy + 8, 24, 20, 8, 8);

                    // Casco y pluma dorada
                    g2.setColor(new Color(220, 175, 45)); // Pluma dorada
                    g2.fillRect(hx + 12, hy - 4, 8, 5);
                    g2.setColor(new Color(145, 150, 165)); // Casco
                    g2.fillOval(hx + 6, hy, 20, 12);
                    g2.setColor(new Color(70, 75, 85));
                    g2.drawOval(hx + 6, hy, 20, 12);
                    
                    // Visor brillante
                    g2.setColor(new Color(0, 230, 255));
                    int vsX = isFacingLeft ? hx + 8 : hx + 18;
                    g2.fillRect(vsX, hy + 4, 6, 3);

                    // Piernas
                    g2.setColor(new Color(80, 85, 95));
                    int legAnim = (int) (Math.sin(time * 0.26) * 6);
                    if (!hero.isOnGround()) {
                        legAnim = 5;
                    } else if (!leftPressed && !rightPressed) {
                        legAnim = 0;
                    }
                    g2.fillRect(hx + 8, hy + 28, 6, 4 + Math.abs(legAnim));
                    g2.fillRect(hx + 18, hy + 28, 6, 4 + Math.max(0, -legAnim));
                }

                // Dibujar Arma en mano del Héroe (solo si no está ya integrada en la animación)
                boolean hasBakedAnimation = "Sword".equalsIgnoreCase(weaponType) || "Bow".equalsIgnoreCase(weaponType);
                if (!hasBakedAnimation) {
                    BufferedImage weaponImg = AssetManager.getInstance().getWeaponSprite(weaponType);
                    if (weaponImg != null) {
                        int wSize = 24;
                        int wX = isFacingLeft ? hx + 4 - wSize : hx + 28;
                        int wY = hy + 8;
                        g2.drawImage(weaponImg, wX, wY, wSize, wSize, null);
                    } else {
                        g2.setColor(new Color(230, 230, 230));
                        g2.setStroke(new BasicStroke(2.0f));
                        
                        int wX = isFacingLeft ? hx - 2 : hx + 34;
                        int wY = hy + 18;
                        
                        if (currentW instanceof com.juego.weapon.Sword) {
                            // Dibujar Espada
                            g2.drawLine(hx + 16, hy + 18, wX, wY - 8);
                            g2.setColor(new Color(220, 180, 50)); // Hilt
                            g2.fillOval(hx + 16, hy + 17, 4, 4);
                        } else if (currentW instanceof com.juego.weapon.Hammer) {
                            // Dibujar Mazo
                            g2.setColor(new Color(90, 90, 95));
                            g2.drawLine(hx + 16, hy + 18, wX, wY - 4);
                            g2.fillRect(wX - 4, wY - 10, 10, 12);
                        } else if (currentW instanceof com.juego.weapon.Bow) {
                            // Dibujar Arco
                            g2.setColor(new Color(139, 90, 43));
                            g2.drawArc(hx + (isFacingLeft ? -6 : 18), hy + 8, 20, 20, isFacingLeft ? 90 : -90, 180);
                        }
                    }
                }
                
                // Efecto de ataque activo (slashing glow)
                if (hero.getCurrentState() instanceof com.juego.entity.AttackingState) {
                    g2.setColor(new Color(255, 230, 100, 160));
                    g2.setStroke(new BasicStroke(3.5f));
                    int arcStart = isFacingLeft ? 135 : -45;
                    g2.drawArc(hx - 10, hy - 10, 52, 52, arcStart, 90);
                }
            }

            // D. Dibujar Enemigos
            for (Enemy enemy : enemyManager.getActiveEnemies()) {
                int ex = (int) enemy.getAbsoluteX();
                int ey = (int) enemy.getAbsoluteY();
                String type = enemy.getFlyweight().getType();
                
                boolean isStunned = false;
                if (enemy instanceof StunnedEnemyDecorator) {
                    isStunned = ((StunnedEnemyDecorator) enemy).isStunned();
                }

                // Dibujar sprites de enemigos (con fallback procedimental)
                boolean enemyFacingLeft = ex > hx;
                BufferedImage enemyImg = AssetManager.getInstance().getEnemySprite(type, isStunned ? "hurt" : "run", time, enemyFacingLeft);
                if (enemyImg != null) {
                    int drawW = 64;
                    int drawH = 64;
                    int drawX = ex + (32 - drawW) / 2;
                    int drawY = ey + 32 - drawH; // Alinear base
                    g2.drawImage(enemyImg, drawX, drawY, drawW, drawH, null);
                } else {
                    if ("Swordsman".equalsIgnoreCase(type)) {
                        // Skeleton Swordsman
                        g2.setColor(new Color(240, 238, 233)); // Bony white
                        g2.fillOval(ex + 4, ey, 16, 16); // Skull
                        g2.drawOval(ex + 4, ey, 16, 16);
                        g2.setColor(new Color(20, 20, 20));
                        g2.fillRect(ex + 8, ey + 6, 2, 2); // Eyes
                        g2.fillRect(ex + 14, ey + 6, 2, 2);
                        
                        g2.setColor(new Color(240, 238, 233));
                        g2.fillRect(ex + 10, ey + 16, 4, 12); // Spine
                        g2.drawLine(ex + 6, ey + 28, ex + 6, ey + 32); // Left leg
                        g2.drawLine(ex + 18, ey + 28, ex + 18, ey + 32); // Right leg
                        
                        g2.setColor(new Color(130, 130, 140)); // Rusty Sword
                        g2.drawLine(ex + 14, ey + 20, ex + 28, ey + 14);
                    } else if ("Shielder".equalsIgnoreCase(type)) {
                        // Shield Goblin
                        g2.setColor(new Color(46, 139, 87)); // Goblin green
                        g2.fillOval(ex + 2, ey + 4, 20, 20); // Head/body
                        g2.setColor(new Color(220, 20, 60)); // Red eyes
                        g2.fillRect(ex + 6, ey + 10, 3, 3);
                        g2.fillRect(ex + 14, ey + 10, 3, 3);
                        
                        // Large wooden shield
                        g2.setColor(new Color(139, 69, 19));
                        g2.fillRoundRect(ex - 4, ey + 2, 8, 26, 4, 4);
                        g2.setColor(new Color(192, 192, 192));
                        g2.fillOval(ex - 2, ey + 11, 4, 4); // Shield boss
                    } else if ("Flyer".equalsIgnoreCase(type)) {
                        // Plagued Gargoyle / Bat
                        g2.setColor(new Color(90, 34, 139)); // Violet
                        g2.fillOval(ex + 4, ey + 4, 16, 16); // Body
                        
                        // Flapping wings
                        int wingY = (int)(Math.sin(time * 0.4) * 8);
                        g2.setColor(new Color(75, 20, 110));
                        int[] wxL = { ex + 4, ex - 12, ex + 4 };
                        int[] wyL = { ey + 12, ey + 4 + wingY, ey + 16 };
                        g2.fillPolygon(wxL, wyL, 3);
                        
                        int[] wxR = { ex + 20, ex + 36, ex + 20 };
                        int[] wyR = { ey + 12, ey + 4 + wingY, ey + 16 };
                        g2.fillPolygon(wxR, wyR, 3);
                        
                        g2.setColor(new Color(255, 60, 60)); // Glowing eyes
                        g2.fillRect(ex + 9, ey + 10, 2, 2);
                        g2.fillRect(ex + 13, ey + 10, 2, 2);
                    }
                }

                // Stun overlay (decorator stars)
                if (isStunned) {
                    g2.setColor(new Color(0, 240, 255));
                    g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                    g2.drawString("★ ★ ★", ex - 2, ey - 14);
                    g2.drawArc(ex - 4, ey - 8, 32, 8, (int)(time * 20 % 360), 180);
                }

                // Health bar above head
                g2.setColor(new Color(200, 30, 30));
                g2.fillRect(ex - 4, ey - 8, 32, 3);
                g2.setColor(new Color(40, 200, 40));
                int barWidth = (int) (32 * (enemy.getHealth() / enemy.getFlyweight().getBaseHealth()));
                if (barWidth > 32) barWidth = 32;
                g2.fillRect(ex - 4, ey - 8, barWidth, 3);
            }

            // Restablecer la transformación para el HUD fijo
            g2.translate(cameraX, 0);

            // ====================================================
            // 5. HUD (Heads-Up Display)
            // ====================================================
            ScoreManager scoreManager = ScoreManager.getInstance();

            // Vidas (Corazones neón)
            g2.setFont(new Font("Georgia", Font.BOLD, 18));
            g2.setColor(new Color(255, 60, 60));
            StringBuilder hearts = new StringBuilder();
            for (int i = 0; i < scoreManager.getLives(); i++) {
                hearts.append("❤️ ");
            }
            g2.drawString("LIVES: " + hearts.toString(), 20, 40);

            // Score
            g2.setColor(Color.WHITE);
            g2.drawString("SCORE: " + scoreManager.getScore(), 20, 70);

            // Combo (Observer)
            int combo = scoreManager.getComboCount();
            if (combo > 0) {
                int mult = (int) Math.pow(2, combo / 3);
                g2.setColor(new Color(255, 215, 0)); // Gold
                g2.setFont(new Font("Georgia", Font.BOLD, 22));
                g2.drawString("COMBO: " + combo + " (x" + mult + ")", 20, 110);
            }

            // Weapon indicators
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("[A/D] Move | [SPACE] Jump | [SHIFT] Dash | [Click Izq] Attack", 20, getHeight() - 40);
            g2.drawString("[1] Sword (Fast) | [2] Hammer (Stun) | [3] Bow (Aim)", 20, getHeight() - 20);
        }

        private Color mixColors(Color c1, Color c2, float ratio) {
            if (ratio < 0.0f) ratio = 0.0f;
            if (ratio > 1.0f) ratio = 1.0f;
            int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
            int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
            int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
            return new Color(r, g, b);
        }
    }
}
