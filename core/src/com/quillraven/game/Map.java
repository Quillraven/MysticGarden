package com.quillraven.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.quillraven.game.ecs.ECSEngine;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class Map {
    private static final String TAG = Map.class.getSimpleName();

    private final TiledMap tiledMap;

    public Map(final TiledMap tiledMap, final ECSEngine ecsEngine) {
        this.tiledMap = tiledMap;

        parseObjectsLayer(tiledMap.getLayers().get("objects"), ecsEngine);
    }

    private void parseObjectsLayer(final MapLayer objectsLayer, final ECSEngine ecsEngine) {
        if (objectsLayer == null) {
            Gdx.app.log(TAG, "Map does not have a layer called 'objects'");
            return;
        }

        for (final MapObject mapObj : objectsLayer.getObjects()) {
            if (mapObj instanceof TiledMapTileMapObject) {
                final TiledMapTileMapObject obj = (TiledMapTileMapObject) mapObj;
                final MapProperties props = obj.getProperties();
                ecsEngine.addGameObject(props.get("x", Float.class) * UNIT_SCALE, props.get("y", Float.class) * UNIT_SCALE, props.get("width", Float.class) * UNIT_SCALE, props.get("height", Float.class) * UNIT_SCALE, obj.getTile());
            }
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
