package com.juego.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spatial Grid Pattern (Optimización de Colisiones):
 * Reduce la complejidad de N^2 a un tiempo casi lineal dividiendo el espacio
 * en celdas (buckets), comprobando colisiones solo entre objetos cercanos.
 */
public class SpatialGrid {
    private int cellSize;
    private Map<String, List<Collider>> cells;

    public SpatialGrid(int cellSize) {
        this.cellSize = cellSize;
        this.cells = new HashMap<>();
    }

    public void clear() {
        cells.clear();
    }

    public void insert(Collider collider) {
        String cellId = getCellId(collider);
        cells.computeIfAbsent(cellId, k -> new ArrayList<>()).add(collider);
    }

    public List<Collider> getNearbyColliders(Collider target) {
        String cellId = getCellId(target);
        // Retorna colisionadores en la misma celda
        return cells.getOrDefault(cellId, new ArrayList<>());
    }

    private String getCellId(Collider collider) {
        int cellX = (int) (collider.getWorldX() / cellSize);
        int cellY = (int) (collider.getWorldY() / cellSize);
        return cellX + "," + cellY;
    }
}
