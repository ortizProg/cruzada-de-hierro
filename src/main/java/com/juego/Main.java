package com.juego;

import com.juego.core.GameManager;
import com.juego.core.ScoreManager;
import com.juego.entity.*;
import com.juego.math.Vector2;
import com.juego.weapon.*;
import com.juego.system.*;
import com.juego.view.HomeView;

public class Main {
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("     INICIALIZANDO SIMULACIÓN DE 12 PATRONES - CRUZADA DE HIERRO");
        System.out.println("======================================================================");

        // -------------------------------------------------------------
        // 1. PATRONES CREACIONALES
        // -------------------------------------------------------------
        System.out.println("\n>>> [A] PATRONES CREACIONALES <<<");

        // [Creacional: Singleton] - ScoreManager ya inicializado
        ScoreManager scoreManager = ScoreManager.getInstance();
        System.out.println("[Singleton] Instancia de ScoreManager obtenida: " + scoreManager.hashCode());

        // [Creacional: Factory Method]
        System.out.println("\n[Factory Method] Generando enemigos procedurales mediante BasicEnemyFactory:");
        EnemyFactory enemyFactory = new BasicEnemyFactory();
        Enemy swordsman = enemyFactory.createEnemy("Swordsman", 50, 100);
        Enemy shielder = enemyFactory.createEnemy("Shielder", 100, 100);
        Enemy flyer = enemyFactory.createEnemy("Flyer", 150, 100);
        System.out.println("  -> Creado: " + swordsman.getFlyweight().getType());
        System.out.println("  -> Creado: " + shielder.getFlyweight().getType());
        System.out.println("  -> Creado: " + flyer.getFlyweight().getType());

        // [Creacional: Builder]
        System.out.println("\n[Builder] Ensamblando configuración compleja de enemigo (Caballero + Escudo + Vida extra):");
        Enemy complexEnemy = new EnemyBuilder("Swordsman", 200, 100)
                .addShield()
                .addExtraHealth(50.0f)
                .addExtraSpeed(1.5f)
                .build();
        System.out.println("  -> Enemigo construido con vida aumentada.");

        // [Creacional: Object Pool]
        System.out.println("\n[Object Pool] Gestionando flechas mediante ArrowPool:");
        ArrowPool arrowPool = new ArrowPool(3); // Pool de tamaño 3
        Arrow arrow1 = arrowPool.acquire();
        Arrow arrow2 = arrowPool.acquire();
        Arrow arrow3 = arrowPool.acquire();
        System.out.println("  -> Adquiridas 3 flechas del pool. Intentando adquirir una cuarta (se expandirá dinámicamente):");
        Arrow arrow4 = arrowPool.acquire(); // Expandir
        System.out.println("  -> Liberando una flecha de vuelta al pool...");
        arrowPool.release(arrow1);

        // -------------------------------------------------------------
        // 2. PATRONES ESTRUCTURALES
        // -------------------------------------------------------------
        System.out.println("\n>>> [B] PATRONES ESTRUCTURALES <<<");

        // [Estructural: Flyweight]
        System.out.println("\n[Flyweight] Comprobando que los datos inmutables de los enemigos se comparten en caché:");
        Enemy swordsman2 = enemyFactory.createEnemy("Swordsman", 300, 100);
        System.out.println("  -> ¿swordsman1 y swordsman2 comparten la misma instancia Flyweight?: " 
                + (swordsman.getFlyweight() == swordsman2.getFlyweight()));

        // [Estructural: Decorator]
        System.out.println("\n[Decorator] Aplicando estado temporal de 'Aturdimiento' a un enemigo golpeado por el Mazo:");
        Enemy stunnedDecorator = new StunnedEnemyDecorator(swordsman, 1000); // 1 segundo de stun
        System.out.println("  -> Intentando mover al enemigo decorado (aturdido):");
        stunnedDecorator.move(2, 0); // Debe impedir el movimiento
        System.out.println("  -> Intentando que el enemigo decorado ataque:");
        stunnedDecorator.attack(); // Debe impedir el ataque

        // [Estructural: Facade]
        System.out.println("\n[Facade] Centralizando el control del Hero a través de HeroFacade:");
        IWeapon sword = new Sword(null);
        Hero hero = new Hero(100, 100, 100, 5, sword);
        HeroFacade heroFacade = new HeroFacade(hero);
        System.out.println("  -> Invocando comandos simples en la fachada:");
        heroFacade.moveRight();
        heroFacade.jump();

        // [Estructural: Bridge]
        System.out.println("\n[Bridge] Desacoplando hardware físico de entrada mediante InputBridge:");
        KeyboardInputDevice keyboard = new KeyboardInputDevice("SPACE");
        InputBridge inputBridge = new GameInputHandler(keyboard);
        System.out.println("  -> Entrada física (Teclado - Tecla SPACE) conectada:");
        inputBridge.handleInput(heroFacade); // Ejecutará salto a través de Bridge

        System.out.println("  -> Cambiando dispositivo en caliente a Gamepad (Botón X):");
        GamepadInputDevice gamepad = new GamepadInputDevice("BUTTON_X");
        inputBridge.setInputDevice(gamepad);
        inputBridge.handleInput(heroFacade); // Ejecutará dash a través de Bridge

        // -------------------------------------------------------------
        // 3. PATRONES DE COMPORTAMIENTO
        // -------------------------------------------------------------
        System.out.println("\n>>> [C] PATRONES DE COMPORTAMIENTO <<<");

        // [Comportamiento: Strategy]
        System.out.println("\n[Strategy] Intercambiando armas en tiempo de ejecución en el héroe:");
        IWeapon hammer = new Hammer(null);
        IWeapon bow = new Bow(arrowPool, new java.util.ArrayList<>());

        System.out.println("  -> Atacando con Espada (Equipada por defecto):");
        heroFacade.attack();

        System.out.println("  -> Equipando Mazo:");
        heroFacade.changeWeapon(hammer);
        heroFacade.attack();

        System.out.println("  -> Equipando Arco (Apuntado y disparo en diagonal arriba-derecha 1, 1):");
        heroFacade.changeWeapon(bow);
        heroFacade.attackDirection(new Vector2(1, 1));

        // [Comportamiento: State]
        System.out.println("\n[State] Transicionando a través de la máquina de estados del Héroe:");
        System.out.println("  -> Estado inicial:");
        heroFacade.update();
        System.out.println("  -> Pulsando MOVE_RIGHT:");
        heroFacade.moveRight(); // Transiciona a RunningState
        System.out.println("  -> Pulsando JUMP:");
        heroFacade.jump(); // Transiciona a JumpingState
        System.out.println("  -> Pulsando DASH:");
        heroFacade.dash(); // Transiciona a DashingState
        System.out.println("  -> Actualizando frame en DashingState:");
        heroFacade.update();

        // [Comportamiento: Command]
        System.out.println("\n[Command] Encapsulando acciones y enviándolas al Facade:");
        ICommand jumpCmd = new JumpCommand();
        ICommand dashCmd = new DashCommand();
        jumpCmd.execute(heroFacade);
        dashCmd.execute(heroFacade);

        // [Comportamiento: Observer]
        System.out.println("\n[Observer] Notificando muertes de enemigos a ScoreManager con multiplicador exponencial:");
        EnemyManager enemyManager = new EnemyManager(5, 10);
        enemyManager.addDeathObserver(scoreManager);
        
        Enemy enemy1 = enemyFactory.createEnemy("Swordsman", 200, 100);
        Enemy enemy2 = enemyFactory.createEnemy("Swordsman", 220, 100);
        Enemy enemy3 = enemyFactory.createEnemy("Swordsman", 240, 100);
        Enemy enemy4 = enemyFactory.createEnemy("Swordsman", 260, 100);
        Enemy enemy5 = enemyFactory.createEnemy("Swordsman", 280, 100);
        
        enemyManager.spawnEnemies(new Vector2(200, 100));
        enemyManager.spawnEnemies(new Vector2(220, 100));
        enemyManager.spawnEnemies(new Vector2(240, 100));

        System.out.println("  -> Score inicial: " + scoreManager.getScore() + " | Combo: " + scoreManager.getComboCount() + " | Vidas: " + scoreManager.getLives());
        
        System.out.println("\n--- Muertes rápidas consecutivas ---");
        enemyManager.dieEnemy(enemy1); // Combo 1, Mult x1 (+100 pts)
        enemyManager.dieEnemy(enemy2); // Combo 2, Mult x1 (+100 pts)
        enemyManager.dieEnemy(enemy3); // Combo 3, Mult x2 (+200 pts)
        enemyManager.dieEnemy(enemy4); // Combo 4, Mult x2 (+200 pts)
        
        System.out.println("\nScore actual: " + scoreManager.getScore() + " | Combo actual: " + scoreManager.getComboCount());

        try {
            System.out.println("\nSimulando espera de 10.5 segundos para la expiración del combo (GDD: 10s)...");
            Thread.sleep(10500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- Muerte tras la expiración del combo ---");
        enemyManager.dieEnemy(enemy5); // Combo expiró -> Combo 1, Mult x1 (+100 pts)
        System.out.println("Score final: " + scoreManager.getScore() + " | Combo final: " + scoreManager.getComboCount());

        System.out.println("\n--- Reduciendo vidas ---");
        scoreManager.decrementLives(); // Pierde una vida

        System.out.println("\n======================================================================");
        System.out.println("     SIMULACIÓN COMPLETA - LOS 12 PATRONES FUNCIONAN PERFECTAMENTE");
        System.out.println("======================================================================");

        // Arrancar la interfaz gráfica del juego por defecto
        System.out.println("\nIniciando interfaz gráfica del juego (Swing)...");
        GameManager.getInstance().addView(new HomeView());
    }
}
