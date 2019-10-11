package com.quillraven.game.map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.quillraven.game.core.ecs.component.ParticleEffectComponent;
import com.quillraven.game.ecs.component.GameObjectComponent;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class GameObject {
    private final int id;
    private final TiledMapTileMapObject tileMapObjectRef;
    private final Rectangle boundaries;
    private final float animationInterval;
    private final GameObjectComponent.GameObjectType type;

    private final ParticleEffectComponent.ParticleEffectType peType;
    private final float peOffsetX;
    private final float peOffsetY;
    private final float peScale;

    private final LightData lightData;

    GameObject(final TiledMapTileMapObject tileMapObject) {
        final MapProperties props = tileMapObject.getProperties();
        final MapProperties tileProps;
        if (tileMapObject.getTile() instanceof AnimatedTiledMapTile) {
            tileProps = ((AnimatedTiledMapTile) tileMapObject.getTile()).getFrameTiles()[0].getProperties();
        } else {
            tileProps = tileMapObject.getTile().getProperties();
        }

        this.tileMapObjectRef = tileMapObject;
        this.id = props.get("id", Integer.class);
        this.boundaries = new Rectangle();
        boundaries.setPosition(props.get("x", Float.class) * UNIT_SCALE, props.get("y", Float.class) * UNIT_SCALE);
        boundaries.setSize(props.get("width", Float.class) * UNIT_SCALE, props.get("height", Float.class) * UNIT_SCALE);

        if (id == 51) {
            System.out.println();
        }

        if (props.containsKey("type")) {
            // type is specified specifically for that object
            type = GameObjectComponent.GameObjectType.valueOf(props.get("type", String.class));
        } else if (tileProps.containsKey("type")) {
            // type is specified generally in the tileset
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

        if (tileProps.containsKey("effect_type")) {
            peType = ParticleEffectComponent.ParticleEffectType.valueOf(tileProps.get("effect_type", String.class));
            peOffsetX = tileProps.get("effect_offset_x", 0f, Float.class);
            peOffsetY = tileProps.get("effect_offset_y", 0f, Float.class);
            peScale = tileProps.get("effect_scale", 1f, Float.class);
        } else {
            peType = ParticleEffectComponent.ParticleEffectType.NOT_DEFINED;
            peOffsetX = 0;
            peOffsetY = 0;
            peScale = 1;
        }
    }

    public int getId() {
        return id;
    }

    public Rectangle getBoundaries() {
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

    public LightData getLightData() {
        return lightData;
    }

    public ParticleEffectComponent.ParticleEffectType getParticleType() {
        return peType;
    }

    public float getParticleOffsetX() {
        return peOffsetX;
    }

    public float getParticleOffsetY() {
        return peOffsetY;
    }

    public float getParticleScale() {
        return peScale;
    }
}
