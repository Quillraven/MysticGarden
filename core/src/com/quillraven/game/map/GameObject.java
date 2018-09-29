package com.quillraven.game.map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.quillraven.game.ecs.component.GameObjectComponent;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

class GameObject {
    private final int id;
    private final TiledMapTileMapObject tileMapObjectRef;
    private final Rectangle boundaries;
    private final float animationInterval;
    private final GameObjectComponent.GameObjectType type;

    private final LightData lightData;

    GameObject(final TiledMapTileMapObject tileMapObject) {
        final MapProperties props = tileMapObject.getProperties();
        final MapProperties tileProps = tileMapObject.getTile().getProperties();

        this.tileMapObjectRef = tileMapObject;
        this.id = props.get("id", Integer.class);
        this.boundaries = new Rectangle();
        boundaries.setPosition(props.get("x", Float.class) * UNIT_SCALE, props.get("y", Float.class) * UNIT_SCALE);
        boundaries.setSize(props.get("width", Float.class) * UNIT_SCALE, props.get("height", Float.class) * UNIT_SCALE);

        if (tileProps.containsKey("type")) {
            type = GameObjectComponent.GameObjectType.valueOf(tileProps.get("type", String.class));
        } else {
            type = GameObjectComponent.GameObjectType.NOT_DEFINED;
        }

        if (tileMapObject.getTile() instanceof AnimatedTiledMapTile) {
            animationInterval = ((AnimatedTiledMapTile) tileMapObject.getTile()).getAnimationIntervals()[0] * 0.001f;
        } else {
            animationInterval = 0;
        }

        if (tileProps.containsKey("light_type")) {
            lightData = new LightData(tileProps);
        } else {
            lightData = null;
        }

    }

    int getId() {
        return id;
    }

    Rectangle getBoundaries() {
        return boundaries;
    }

    float getAnimationInterval() {
        return animationInterval;
    }

    TiledMapTile getTile() {
        return tileMapObjectRef.getTile();
    }

    public GameObjectComponent.GameObjectType getType() {
        return type;
    }

    LightData getLightData() {
        return lightData;
    }
}
