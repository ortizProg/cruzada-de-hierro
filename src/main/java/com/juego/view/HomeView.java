package com.juego.view;

import com.juego.core.GameManager;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * HomeView representa la pantalla de inicio del juego "Cruzada de Hierro".
 * Rediseñada con una estética Medieval RPG de paisaje diurno soleado y cenizas de la plaga.
 */
public class HomeView implements View {
    private JPanel panel;

    public HomeView() {
        this.panel = new HomeCanvasPanel();
        this.render();
    }

    private void render() {
        // Usar BoxLayout vertical para apilar el título y los botones
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Espaciador inicial
        panel.add(Box.createRigidArea(new Dimension(0, 100)));

        // Botones interactivos medievales de piedra tallada y bordes de hierro/oro
        GothicButton startButton = new GothicButton("COMENZAR AVENTURA  ⚔", false);
        startButton.setMaximumSize(new Dimension(320, 50));
        startButton.setPreferredSize(new Dimension(320, 50));
        startButton.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        startButton.addActionListener((ActionEvent e) -> startGame());

        GothicButton controlsButton = new GothicButton("CONTROLES Y LEYENDA  📖", false);
        controlsButton.setMaximumSize(new Dimension(320, 50));
        controlsButton.setPreferredSize(new Dimension(320, 50));
        controlsButton.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        controlsButton.addActionListener((ActionEvent e) -> showControls());

        GothicButton exitButton = new GothicButton("ABANDONAR  ⏻", true);
        exitButton.setMaximumSize(new Dimension(320, 50));
        exitButton.setPreferredSize(new Dimension(320, 50));
        exitButton.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        exitButton.addActionListener((ActionEvent e) -> System.exit(0));

        // Añadir botones y espaciadores
        panel.add(Box.createRigidArea(new Dimension(0, 160))); // Dejar espacio para el título y arco gótico
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(controlsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(exitButton);
        
        System.out.println("UI para HomeView construida y renderizada con estilo Medieval RPG (Día)");
    }

    private void showControls() {
        String msg = "--- CRUZADA DE HIERRO ---\n\n"
                + "Trama:\n"
                + "Sir Kaelen recorre el reino de Valoria purgando \"La Plaga de Ceniza\"\n"
                + "y enfrentando hordas de Esqueletos, Duendes y gárgolas voladoras\n"
                + "con armas dinámicas de combate medieval.\n\n"
                + "Controles:\n"
                + "- [A / D] o Flechas: Moverse a izquierda / derecha\n"
                + "- [SPACE]: Saltar (doble salto en el aire apoyándose en muros)\n"
                + "- [SHIFT]: Dash terrestre o aéreo invulnerable a golpes\n"
                + "- [J]: Atacar con el arma equipada (apuntado en 8 direcciones)\n\n"
                + "Armas del Arsenal (Estrategias de Combate):\n"
                + "- [Tecla 1]: Espada Medieval (Ataques rápidos, ideal contra duendes y plaga común)\n"
                + "- [Tecla 2]: Mazo de Hierro (Ataque pesado y lento, rompe escudos y aplica ATURDIMIENTO)\n"
                + "- [Tecla 3]: Arco Real (Disparo de proyectiles rápidos a distancia usando Object Pool)\n\n"
                + "Puntaje y Combo de Kills:\n"
                + "- Derrota enemigos para acumular puntos.\n"
                + "- Elimina monstruos de forma consecutiva para disparar tu multiplicador exponencial.\n"
                + "- ¡Cuidado! El combo expira tras 10 segundos de inactividad de combate.";
        
        JOptionPane.showMessageDialog(panel, msg, "Controles y Leyenda", JOptionPane.INFORMATION_MESSAGE);
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
        if (panel instanceof HomeCanvasPanel) {
            ((HomeCanvasPanel) panel).stopTimer();
        }
    }

    public void startGame() {
        System.out.println("Starting game...");
        GameManager.getInstance().addView(new GameplayView());
        GameManager.getInstance().removeView(this);
    }

    public void exitGame() {
        GameManager.getInstance().removeView(this);
    }

    /**
     * Panel de dibujo personalizado con estética medieval skeuomórfica,
     * columnas de castillo, arco decorativo, sol radiante, nubes flotantes
     * y partículas de ceniza y polen de sol.
     */
    private class HomeCanvasPanel extends JPanel {
        private List<StartParticle> particles = new ArrayList<>();
        private Timer repaintTimer;
        private double time = 0;
        private final int PARTICLE_COUNT = 70;

        public HomeCanvasPanel() {
            // Inicializar partículas
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                particles.add(new StartParticle(800, 600));
            }

            // Iniciar el temporizador para actualizar partículas y repintar
            repaintTimer = new Timer(16, (ActionEvent e) -> {
                time += 0.5;
                int w = getWidth();
                int h = getHeight();
                for (StartParticle p : particles) {
                    p.update(time, w, h);
                }
                repaint();
            });
            repaintTimer.start();
        }

        public void stopTimer() {
            if (repaintTimer != null && repaintTimer.isRunning()) {
                repaintTimer.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // ==========================================
            // 1. DIBUJAR PAISAJE DIURNO DE FONDO
            // ==========================================

            // A. Cielo diurno medieval de mañana clara (Gradiente de Azul Claro a Dorado)
            GradientPaint sky = new GradientPaint(
                0, 0, new Color(135, 206, 250),     // Azul cielo claro
                0, h, new Color(253, 245, 222)      // Dorado suave en el horizonte
            );
            g2.setPaint(sky);
            g2.fillRect(0, 0, w, h);

            // B. Sol radiante diurno en la esquina superior derecha
            int sunX = w - 160;
            int sunY = 60;
            int sunSize = 55;
            // Rayos y brillo solar
            g2.setColor(new Color(255, 235, 180, 20));
            g2.fillOval(sunX - 25, sunY - 25, sunSize + 50, sunSize + 50);
            g2.setColor(new Color(255, 245, 200, 50));
            g2.fillOval(sunX - 10, sunY - 10, sunSize + 20, sunSize + 20);
            // Cuerpo del sol
            g2.setColor(new Color(255, 245, 200));
            g2.fillOval(sunX, sunY, sunSize, sunSize);

            // C. Nubes flotantes y dinámicas (se desplazan despacio con 'time')
            for (int i = 0; i < 4; i++) {
                double cloudX = (i * 240 + time * 0.1) % (w + 160) - 100;
                int cloudY = 40 + i * 22;
                g2.setColor(new Color(255, 255, 255, 150));
                g2.fillOval((int)cloudX, cloudY, 55, 30);
                g2.fillOval((int)cloudX + 15, cloudY - 8, 45, 35);
                g2.fillOval((int)cloudX + 35, cloudY + 4, 40, 25);
            }

            // D. Montañas distantes (Capa 1 de montañas en azul brumoso diurno)
            int[] m1X = { 0, w / 4, w / 2, 3 * w / 4, w, w, 0 };
            int[] m1Y = { h - 180, h - 235, h - 190, h - 250, h - 210, h, h };
            g2.setColor(new Color(110, 142, 163)); // Azul grisáceo diurno
            g2.fillPolygon(m1X, m1Y, 7);

            // E. Silueta del Castillo de Valoria en la cumbre
            int cx = (int) (w * 0.71);
            int cy = h - 250;
            g2.setColor(new Color(75, 91, 107)); // Gris piedra iluminado
            g2.fillRect(cx, cy, 35, 45); // Torre central
            g2.fillRect(cx - 8, cy + 10, 8, 35); // Torre izquierda
            g2.fillRect(cx + 35, cy + 10, 8, 35); // Torre derecha
            // Techos cónicos
            int[] rx1 = { cx - 8, cx - 4, cx };
            int[] ry1 = { cy + 10, cy, cy + 10 };
            g2.fillPolygon(rx1, ry1, 3);
            int[] rx2 = { cx + 35, cx + 39, cx + 43 };
            int[] ry2 = { cy + 10, cy, cy + 10 };
            g2.fillPolygon(rx2, ry2, 3);
            // Almenas
            g2.fillRect(cx + 4, cy - 6, 6, 6);
            g2.fillRect(cx + 14, cy - 6, 6, 6);
            g2.fillRect(cx + 24, cy - 6, 6, 6);

            // F. Colinas intermedias cubiertas de bosque verde (Capa 2)
            int[] m2X = { 0, w / 3, 2 * w / 3, w, w, 0 };
            int[] m2Y = { h - 120, h - 150, h - 130, h - 160, h, h };
            g2.setColor(new Color(46, 110, 75)); // Verde pradera diurno
            g2.fillPolygon(m2X, m2Y, 6);

            // Pinos sobre las colinas
            g2.setColor(new Color(30, 80, 52));
            for (int i = 0; i < 10; i++) {
                int tx = 60 + i * (w - 120) / 9;
                double ratio = (double) tx / w;
                int ty = (int) (h - 120 - (ratio * 40));
                if (i == 3) ty = h - 146;
                if (i == 6) ty = h - 132;
                
                int treeW = 10;
                int treeH = 26;
                int[] px = { tx, tx - treeW, tx + treeW };
                int[] py = { ty - treeH, ty, ty };
                g2.fillPolygon(px, py, 3);
                
                int[] px2 = { tx, tx - (int)(treeW * 0.7), tx + (int)(treeW * 0.7) };
                int[] py2 = { ty - treeH - 8, ty - 8, ty - 8 };
                g2.fillPolygon(px2, py2, 3);
            }

            // G. Loma de pradera en primer plano (Capa 3)
            int[] m3X = { 0, w / 2, w, w, 0 };
            int[] m3Y = { h - 60, h - 80, h - 70, h, h };
            g2.setColor(new Color(25, 60, 36)); // Verde prado cercano
            g2.fillPolygon(m3X, m3Y, 5);

            // ==========================================
            // 2. RESPLANDOR Y ESTRUCTURAS DE SOPORTE
            // ==========================================

            // Resplandor radial de sol templado detrás del título
            float[] fractions = { 0.0f, 1.0f };
            Color[] colors = { new Color(255, 230, 150, 22), new Color(253, 245, 222, 0) };
            RadialGradientPaint radialGlow = new RadialGradientPaint(
                w / 2.0f, 160.0f, 440.0f,
                fractions, colors
            );
            g2.setPaint(radialGlow);
            g2.fillOval(w / 2 - 440, -60, 880, 440);

            // Arco de piedra enmarcador de botones (translúcido sobre el paisaje)
            g2.setColor(new Color(212, 175, 55, 20));
            g2.setStroke(new BasicStroke(3.0f));
            int archW = 420;
            int archH = 340;
            int archX = (w - archW) / 2;
            int archY = 230;
            g2.drawRoundRect(archX, archY, archW, archH, 80, 80);

            // Columnas laterales de castillo
            // Columna izquierda
            g2.setPaint(new GradientPaint(0, 0, new Color(40, 36, 30), 40, 0, new Color(22, 19, 17)));
            g2.fillRect(0, 0, 40, h);
            g2.setColor(new Color(212, 175, 55, 35));
            g2.drawLine(40, 0, 40, h);
            
            // Columna derecha
            g2.setPaint(new GradientPaint(w - 40, 0, new Color(22, 19, 17), w, 0, new Color(40, 36, 30)));
            g2.fillRect(w - 40, 0, 40, h);
            g2.setColor(new Color(212, 175, 55, 35));
            g2.drawLine(w - 40, 0, w - 40, h);

            // Anillos divisorios en las columnas
            g2.setColor(new Color(16, 14, 12));
            for (int y = 100; y < h; y += 180) {
                g2.fillRect(0, y, 40, 10);
                g2.fillRect(w - 40, y, 40, 10);
            }

            // ==========================================
            // 3. EFECTOS CLAVE, LOGO Y FOOTER
            // ==========================================

            // Partículas de brasas y ceniza volando sobre el paisaje
            for (StartParticle p : particles) {
                p.draw(g2);
            }

            // Título "CRUZADA DE HIERRO"
            String titleText = "CRUZADA DE HIERRO";
            g2.setFont(new Font("Georgia", Font.BOLD, 54));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(titleText)) / 2;
            int ty = 160;

            // Brillo neón fuego/sol detrás del logo
            double breath = (Math.sin(time * 0.035) + 1.0) / 2.0;
            int alphaGlow = 40 + (int) (breath * 60);
            g2.setColor(new Color(254, 101, 0, alphaGlow));
            g2.drawString(titleText, tx - 3, ty + 3);
            g2.drawString(titleText, tx + 3, ty - 3);
            g2.drawString(titleText, tx - 1, ty - 1);
            g2.drawString(titleText, tx + 1, ty + 1);

            // Contorno oscuro chiseled
            g2.setColor(new Color(26, 17, 10));
            g2.drawString(titleText, tx - 2, ty);
            g2.drawString(titleText, tx + 2, ty);
            g2.drawString(titleText, tx, ty - 2);
            g2.drawString(titleText, tx, ty + 2);

            // Relleno de degradado de oro
            GradientPaint goldGradient = new GradientPaint(
                tx, ty - 45, new Color(255, 224, 136),
                tx, ty, new Color(212, 175, 55)
            );
            g2.setPaint(goldGradient);
            g2.drawString(titleText, tx, ty);

            // Subtítulo
            String subtitleText = "P U R G A   L A   P L A G A   D E   C E N I Z A";
            g2.setFont(new Font("Sora", Font.BOLD, 13));
            fm = g2.getFontMetrics();
            int sx = (w - fm.stringWidth(subtitleText)) / 2;
            g2.setColor(new Color(212, 175, 55));
            g2.drawString(subtitleText, sx, ty + 38);

            // Línea del footer y créditos de pergamino
            g2.setColor(new Color(212, 175, 55, 25));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(50, h - 50, w - 50, h - 50);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.setColor(new Color(208, 197, 175, 120));
            
            g2.drawString("VALORIA ENGINE - v1.0.0", 55, h - 25);
            String rightText = "TÉRMINOS DE SERVICIO  |  PRIVACIDAD";
            int rx = w - g2.getFontMetrics().stringWidth(rightText) - 55;
            g2.drawString(rightText, rx, h - 25);
        }
    }

    /**
     * Clase interna que representa una partícula individual (Brasa ardiente naranja o ceniza plaga gris)
     */
    private class StartParticle {
        double x, y;
        double size;
        double vx, vy;
        double opacity;
        double baseOpacity;
        double pulseSpeed;
        double pulseOffset;
        boolean isEmber;

        public StartParticle(int width, int height) {
            reset(width, height);
            this.y = Math.random() * height;
        }

        public void reset(int width, int height) {
            int w = width > 0 ? width : 800;
            int h = height > 0 ? height : 600;
            // Spawnear entre las columnas para no pisar bordes
            this.x = 45 + Math.random() * (w - 90);
            this.y = h + Math.random() * 50;
            this.size = Math.random() * 2.2 + 0.8;
            this.baseOpacity = Math.random() * 0.55 + 0.15;
            this.opacity = this.baseOpacity;
            // Ascenso vertical suave con sutil deriva
            this.vy = -(Math.random() * 0.7 + 0.3);
            this.vx = (Math.random() - 0.5) * 0.3;
            // 40% de ser brasa dorada brillante de polen, 60% ceniza gris de la plaga
            this.isEmber = Math.random() > 0.60;
            this.pulseSpeed = Math.random() * 0.05 + 0.02;
            this.pulseOffset = Math.random() * Math.PI * 2;
        }

        public void update(double time, int width, int height) {
            int w = width > 0 ? width : 800;
            int h = height > 0 ? height : 600;
            this.y += this.vy;
            this.x += this.vx;
            // Deriva sinusoidal de aire caliente
            this.x += Math.sin(time * 0.015 + this.y * 0.01) * 0.2;
            
            // Pulsación de brillo
            this.opacity = this.baseOpacity + Math.sin(time * this.pulseSpeed + this.pulseOffset) * 0.15;
            if (this.opacity < 0) this.opacity = 0;
            if (this.opacity > 1) this.opacity = 1;

            if (this.y < -10 || this.x < 40 || this.x > w - 40) {
                reset(w, h);
            }
        }

        public void draw(Graphics2D g2) {
            int alpha = (int) (this.opacity * 255);
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;
            
            Color c;
            Color glowC;
            if (isEmber) {
                // Brasa dorada / polen brillante (#ffd700)
                c = new Color(255, 215, 0, alpha);
                glowC = new Color(254, 160, 0, alpha / 3);
            } else {
                // Ceniza gris de plaga (#a0a0a0)
                c = new Color(150, 145, 140, alpha);
                glowC = new Color(76, 68, 60, alpha / 4);
            }

            // Brillo radiante
            g2.setColor(glowC);
            g2.fillOval((int)(x - size * 2.0), (int)(y - size * 2.0), (int)(size * 5.0), (int)(size * 5.0));

            // Centro
            g2.setColor(c);
            g2.fillOval((int)(x - size / 2.0), (int)(y - size / 2.0), (int)size, (int)size);
        }
    }

    /**
     * Botón de estilo Medieval "Forged Iron-Stone" con esquinas biseladas (notches)
     * e iluminación dorada/naranja sobre hover.
     */
    private class GothicButton extends JButton {
        private Color neonColor;
        private Color hoverColor;
        private boolean isHovered = false;
        private boolean isPressed = false;
        private boolean isCrimson = false;

        public GothicButton(String text, boolean isCrimson) {
            super(text);
            this.isCrimson = isCrimson;
            if (isCrimson) {
                // Fuego Carmesí / Sangre para Salir
                this.neonColor = new Color(164, 2, 23);       // Carmesí apagado
                this.hoverColor = new Color(255, 120, 110);   // Carmesí encendido
            } else {
                // Oro / Fuego Naranja para comenzar/controles
                this.neonColor = new Color(212, 175, 55);     // Oro viejo #d4af37
                this.hoverColor = new Color(254, 101, 0);     // Naranja de antorcha #ff6600
            }

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Georgia", Font.BOLD, 14)); // Georgia medieval para botones
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Muesca / Biselado medieval de 10px en esquinas alternas
            int[] xPoints = { 10, w - 1, w - 1, w - 10, 0, 0 };
            int[] yPoints = { 0, 0, h - 10, h - 1, h - 1, 10 };
            java.awt.Polygon gothicPolygon = new java.awt.Polygon(xPoints, yPoints, 6);

            // Fondo de piedra pulida semitransparente (glassmorphic stone)
            GradientPaint btnBg = new GradientPaint(
                0, 0, isHovered ? new Color(55, 49, 44, 210) : new Color(42, 37, 33, 170),
                0, h, isHovered ? new Color(34, 28, 24, 230) : new Color(24, 20, 18, 190)
            );
            
            g2.setPaint(btnBg);
            g2.fillPolygon(gothicPolygon);

            // Dibujar borde biselado forjado
            float strokeWidth = isHovered ? 2.5f : 1.5f;

            if (isHovered) {
                // Brillo difuso (bloom)
                g2.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), 60));
                g2.setStroke(new BasicStroke(strokeWidth + 2.0f));
                g2.drawPolygon(gothicPolygon);

                // Borde primario
                g2.setColor(hoverColor);
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.drawPolygon(gothicPolygon);
            } else {
                // Borde atenuado metálico
                g2.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), 110));
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.drawPolygon(gothicPolygon);
            }

            // Dibujar texto centrado
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h + fm.getAscent()) / 2 - 2;

            if (isPressed) {
                ty += 1;
            }

            if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(new Color(232, 226, 208)); // Parchment white text #e8e2d0
            }
            g2.drawString(getText(), tx, ty);
        }
    }
}
