package com.juego.system;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

/**
 * [Patrón Creacional: Singleton]
 * AssetManager centraliza la carga, caché, corte de animaciones y volteado
 * de sprites para el juego.
 * Incorpora un mecanismo de Fallback: si las imágenes no existen en el
 * classpath, retorna null de forma segura, permitiendo que GameplayView
 * dibuje las figuras procedimentales por defecto sin romperse.
 */
public class AssetManager {
    private static AssetManager instance;
    private final Map<String, BufferedImage> spriteCache;
    private final Map<String, BufferedImage[]> slicedCache;
    private final Map<String, BufferedImage> flippedFrameCache;
    private boolean warningLogged = false;

    private AssetManager() {
        this.spriteCache = new HashMap<>();
        this.slicedCache = new HashMap<>();
        this.flippedFrameCache = new HashMap<>();
    }

    public static synchronized AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    /**
     * Intenta cargar una imagen desde el classpath (/assets/nom_archivo.png).
     * Si no existe o falla, retorna null. Cachea el resultado para no re-leer el disco.
     */
    public BufferedImage getSprite(String path) {
        if (spriteCache.containsKey(path)) {
            return spriteCache.get(path);
        }

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                spriteCache.put(path, img);
                System.out.println("[AssetManager] Cargado correctamente: " + path);
                return img;
            } else {
                if (!warningLogged) {
                    System.out.println("[AssetManager] Nota: No se encontraron recursos PNG en '" + path + "'. Se usará el renderizado procedimental por defecto.");
                }
                spriteCache.put(path, null); // Evitar re-intentos de carga fallida
                return null;
            }
        } catch (Exception e) {
            System.err.println("[AssetManager] Error al cargar la imagen " + path + ": " + e.getMessage());
            spriteCache.put(path, null);
            return null;
        } finally {
            warningLogged = true; // Evitar spam en consola
        }
    }

    /**
     * Carga y divide un spritesheet en un array de frames individuales, cacheándolo.
     */
    private BufferedImage[] getSlicedFrames(String path, int frameWidth, int frameHeight) {
        String cacheKey = path + "_" + frameWidth + "x" + frameHeight;
        if (slicedCache.containsKey(cacheKey)) {
            return slicedCache.get(cacheKey);
        }

        BufferedImage sheet = getSprite(path);
        if (sheet == null) {
            slicedCache.put(cacheKey, null);
            return null;
        }

        int numFrames = sheet.getWidth() / frameWidth;
        if (numFrames <= 0) {
            BufferedImage[] single = new BufferedImage[]{ sheet };
            slicedCache.put(cacheKey, single);
            return single;
        }

        BufferedImage[] frames = new BufferedImage[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }
        slicedCache.put(cacheKey, frames);
        return frames;
    }

    /**
     * Voltea horizontalmente una imagen y la guarda en la caché.
     */
    private BufferedImage getFlippedFrame(String key, BufferedImage frame) {
        if (flippedFrameCache.containsKey(key)) {
            return flippedFrameCache.get(key);
        }
        
        int w = frame.getWidth();
        int h = frame.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.drawImage(frame, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        
        flippedFrameCache.put(key, flipped);
        return flipped;
    }

    /**
     * Obtiene el sprite correspondiente del Héroe según su estado, arma y dirección.
     */
    public BufferedImage getHeroSprite(String state, boolean facingLeft, double time, String weaponType) {
        String weaponSuffix = "bare";
        if ("Sword".equalsIgnoreCase(weaponType)) {
            weaponSuffix = "sword";
        } else if ("Bow".equalsIgnoreCase(weaponType)) {
            weaponSuffix = "bow";
        }

        // 1. Intentar cargar la animación específica con arma (ej. hero_run_sword)
        String path = "/assets/hero_" + state.toLowerCase() + "_" + weaponSuffix + ".png";
        BufferedImage[] frames = getSlicedFrames(path, 256, 256);

        // 2. Si no existe, probar con la versión bare (ej. hero_run_bare)
        if (frames == null) {
            path = "/assets/hero_" + state.toLowerCase() + "_bare.png";
            frames = getSlicedFrames(path, 256, 256);
        }

        if (frames == null || frames.length == 0) {
            return null;
        }

        // Ajustar velocidad de animación por estado
        double speed = 0.15;
        if ("run".equalsIgnoreCase(state)) {
            speed = 0.22;
        } else if ("attack".equalsIgnoreCase(state)) {
            speed = 0.3;
        } else if ("jump".equalsIgnoreCase(state)) {
            speed = 0.12;
        }

        int frameIndex = (int) (time * speed) % frames.length;
        BufferedImage frame = frames[frameIndex];

        if (facingLeft) {
            return getFlippedFrame(path + "_flipped_" + frameIndex, frame);
        }
        return frame;
    }

    /**
     * Obtiene el sprite del arma especificada.
     */
    public BufferedImage getWeaponSprite(String weaponType) {
        return getSprite("/assets/weapon_" + weaponType.toLowerCase() + ".png");
    }

    /**
     * Obtiene el sprite del enemigo según su tipo, estado y dirección.
     */
    public BufferedImage getEnemySprite(String enemyType, String state, double time, boolean facingLeft) {
        String stateSuffix = "idle";
        if ("run".equalsIgnoreCase(state) || "walk".equalsIgnoreCase(state)) {
            stateSuffix = "run";
        } else if ("attack".equalsIgnoreCase(state) || "hurt".equalsIgnoreCase(state)) {
            stateSuffix = state.toLowerCase();
        }

        String path = "/assets/enemy_" + enemyType.toLowerCase() + "_" + stateSuffix + ".png";
        BufferedImage[] frames = getSlicedFrames(path, 64, 64);

        if (frames == null) {
            path = "/assets/enemy_" + enemyType.toLowerCase() + ".png";
            frames = getSlicedFrames(path, 64, 64);
        }

        if (frames == null || frames.length == 0) {
            return null;
        }

        double speed = 0.15;
        int frameIndex = (int) (time * speed) % frames.length;
        BufferedImage frame = frames[frameIndex];

        if (facingLeft) {
            return getFlippedFrame(path + "_flipped_" + frameIndex, frame);
        }
        return frame;
    }

    /**
     * Obtiene el sprite del bloque según su tipo y estilo visual.
     */
    public BufferedImage getTileSprite(String tileType, String style) {
        String path = "/assets/tile_" + tileType.toLowerCase() + "_" + style.toLowerCase() + ".png";
        BufferedImage img = getSprite(path);
        if (img == null) {
            img = getSprite("/assets/tile_" + tileType.toLowerCase() + ".png");
        }
        return img;
    }

    /**
     * Obtiene el sprite del portal.
     */
    public BufferedImage getPortalSprite() {
        return getSprite("/assets/portal.png");
    }
}
