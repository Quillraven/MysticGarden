package com.quillraven.game.map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

class GameObject {
    private final TiledMapTileMapObject tileMapObjectRef;
    private final Rectangle boundaries;
    private final float animationInterval;

    public GameObject(final TiledMapTileMapObject tileMapObject) {
        final MapProperties props = tileMapObject.getProperties();

        this.tileMapObjectRef = tileMapObject;
        this.boundaries = new Rectangle();
        boundaries.setPosition(props.get("x", Float.class) * UNIT_SCALE, props.get("y", Float.class) * UNIT_SCALE);
        boundaries.setSize(props.get("width", Float.class) * UNIT_SCALE, props.get("height", Float.class) * UNIT_SCALE);

        if (tileMapObject.getTile() instanceof AnimatedTiledMapTile) {
            animationInterval = ((AnimatedTiledMapTile) tileMapObject.getTile()).getAnimationIntervals()[0] * 0.001f;
        } else {
            animationInterval = 0;
        }
    }

    public Rectangle getBoundaries() {
        return boundaries;
    }

    public float getAnimationInterval() {
        return animationInterval;
    }

    public TiledMapTile getTile() {
        return tileMapObjectRef.getTile();
    }
}
