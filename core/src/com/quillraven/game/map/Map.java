package com.quillraven.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Map {
    private static final String TAG = Map.class.getSimpleName();

    private final TiledMap tiledMap;
    private final Array<GameObject> gameObjects;
    private final Array<CollisionArea> collisionAreas;
    private final Vector2 startLocation;

    public Map(final TiledMap tiledMap) {
        final MapProperties mapProps = tiledMap.getProperties();
        this.tiledMap = tiledMap;
        this.gameObjects = new Array<>();
        this.collisionAreas = new Array<>();
        this.startLocation = new Vector2(mapProps.get("playerStartTileX", 0f, Float.class), mapProps.get("playerStartTileY", 0f, Float.class));
        parseGameObjects();
        parseCollision();
    }

    private void parseCollision() {
        final MapLayer collisionLayer = tiledMap.getLayers().get("collision");
        if (collisionLayer == null) {
            Gdx.app.log(TAG, "Map does not have a layer called 'collision'");
            return;
        }

        for (final MapObject mapObj : collisionLayer.getObjects()) {
            if (mapObj instanceof PolylineMapObject) {
                final Polyline polyline = ((PolylineMapObject) mapObj).getPolyline();
                collisionAreas.add(new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices()));
            } else {
                Gdx.app.log(TAG, "Unsupported mapObject for collision layer: " + mapObj);
            }
        }
    }

    private void parseGameObjects() {
        final MapLayer objectsLayer = tiledMap.getLayers().get("objects");
        if (objectsLayer == null) {
            Gdx.app.log(TAG, "Map does not have a layer called 'objects'");
            return;
        }

        for (final MapObject mapObj : objectsLayer.getObjects()) {
            if (mapObj instanceof TiledMapTileMapObject) {
                gameObjects.add(new GameObject((TiledMapTileMapObject) mapObj));
            } else {
                Gdx.app.log(TAG, "Unsupported mapObject for objects layer: " + mapObj);
            }
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Array<CollisionArea> getCollisionAreas() {
        return collisionAreas;
    }

    public Vector2 getStartLocation() {
        return startLocation;
    }
}
