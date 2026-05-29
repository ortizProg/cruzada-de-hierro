package com.juego.physics;

/**
 * Representa un bloque o plataforma física en el mapa generado.
 * Implementa Collidable para integrarse con el sistema de colisiones del juego.
 */
public class LevelBlock implements Collidable {
    private float x;
    private float y;
    private float width;
    private float height;
    private String type;   // "GROUND" (suelo sólido), "PLATFORM" (plataforma flotante), "SPIKES" (espinas que dañan)
    private String style;  // "FIELD" (campo abierto) o "CASTLE" (castillo)
    private Collider bodyCollider;

    public LevelBlock(float x, float y, float width, float height, String type, String style) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.style = style;
        this.bodyCollider = new Collider(this, 0, 0, width, height);
    }

    public String getType() {
        return type;
    }

    public String getStyle() {
        return style;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public Collider getCollider() {
        return bodyCollider;
    }

    @Override
    public float getAbsoluteX() {
        return x;
    }

    @Override
    public float getAbsoluteY() {
        return y;
    }
}
