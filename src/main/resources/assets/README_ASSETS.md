# Guía de Assets para "Cruzada de Hierro"

Esta carpeta contiene los sprites e imágenes en formato PNG que el juego cargará automáticamente.

## Estructura de Nombres Obligatoria

Para que el `AssetManager` asocie correctamente tus imágenes a la lógica del juego, debes nombrar los archivos exactamente de la siguiente manera:

### 1. El Héroe (Sir Kaelen)
*   **Reposo (Idle):** `hero_idle_right.png` (mirando a la derecha) y `hero_idle_left.png` (mirando a la izquierda).
*   **Correr (Running):** `hero_run_right.png` y `hero_run_left.png`.
*   **Saltar (Jumping):** `hero_jump_right.png` y `hero_jump_left.png`.
*   **Deslizarse (Dash):** `hero_dash_right.png` y `hero_dash_left.png`.
*   **Atacar (Attacking):** `hero_attack_right.png` y `hero_attack_left.png`.

> *Nota:* Si prefieres no separar por izquierda/derecha, puedes guardar una sola imagen como `hero_idle.png` y el sistema la usará para ambas direcciones (sin voltearla).

### 2. Arsenal (Armas)
*   **Espada:** `weapon_sword.png`
*   **Mazo:** `weapon_hammer.png`
*   **Arco:** `weapon_bow.png`
*   **Proyectil (Flecha):** `projectile_arrow.png`

### 3. Enemigos
*   **Esqueleto Espadachín:** `enemy_swordsman.png`
*   **Duende con Escudo:** `enemy_shielder.png`
*   **Gárgola Voladora:** `enemy_flyer.png`

### 4. Bloques de Nivel (Tiles)
Los bloques se dibujan de forma repetitiva (tiled) en pantalla, por lo que se recomienda usar texturas cuadradas que conecten bien (ej. 32x32 o 64x64 píxeles).

*   **Suelo Campo Abierto (Field):** `tile_ground_field.png`
*   **Suelo Castillo (Castle):** `tile_ground_castle.png`
*   **Plataformas Campo Abierto:** `tile_platform_field.png`
*   **Plataformas Castillo:** `tile_platform_castle.png`
*   **Pinchos Campo Abierto:** `tile_spikes_field.png`
*   **Pinchos Castillo:** `tile_spikes_castle.png`
*   **Portal de Victoria:** `portal.png`

---

## ¿Cómo funciona el sistema de Fallback?
Si dejas esta carpeta vacía o te falta algún sprite, **el juego no se romperá**. El motor detectará la ausencia del archivo PNG y dibujará el personaje o bloque con los gráficos vectoriales procedimentales originales por defecto. Esto te permite ir agregando tus imágenes una a una y probarlas en tiempo real.
