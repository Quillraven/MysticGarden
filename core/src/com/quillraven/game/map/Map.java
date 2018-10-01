package com.quillraven.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.ecs.component.GameObjectComponent;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class Map {
    private static final String TAG = Map.class.getSimpleName();

    private final TiledMap tiledMap;
    private final Array<GameObject> gameObjects;
    private final Array<CollisionArea> collisionAreas;
    private final Vector2 startLocation;
    private final Array<Rectangle> camBoundaries;
    private int numCrystals;

    Map(final TiledMap tiledMap) {
        final MapProperties mapProps = tiledMap.getProperties();
        this.tiledMap = tiledMap;
        this.gameObjects = new Array<>();
        this.collisionAreas = new Array<>();
        this.camBoundaries = new Array<>();
        this.startLocation = new Vector2(mapProps.get("playerStartTileX", 0f, Float.class), mapProps.get("playerStartTileY", 0f, Float.class));
        this.numCrystals = 0;
        parseGameObjects();
        parseCollision();
        parseBoundaries();
    }

    private void parseBoundaries() {
        final MapLayer boundariesLayer = tiledMap.getLayers().get("boundaries");
        if (boundariesLayer == null) {
            Gdx.app.log(TAG, "Map does not have a layer called 'boundaries'");
            return;
        }

        for (final MapObject mapObj : boundariesLayer.getObjects()) {
            if (mapObj instanceof RectangleMapObject) {
                final Rectangle rectangle = new Rectangle(((RectangleMapObject) mapObj).getRectangle());
                rectangle.x *= UNIT_SCALE;
                rectangle.y *= UNIT_SCALE;
                rectangle.width *= UNIT_SCALE;
                rectangle.height *= UNIT_SCALE;
                camBoundaries.add(rectangle);
            } else {
                Gdx.app.log(TAG, "Unsupported mapObject for boundary layer: " + mapObj);
            }
        }
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
                collisionAreas.add(new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices(), mapObj.getProperties().get("isWater", false, Boolean.class)));
            } else if (mapObj instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) mapObj).getRectangle();
                final float[] rectVertices = new float[10];
                // left-bot
                rectVertices[0] = 0;
                rectVertices[1] = 0;
                // left-top
                rectVertices[2] = 0;
                rectVertices[3] = rect.height;
                // right-top
                rectVertices[4] = rect.width;
                rectVertices[5] = rect.height;
                // right-bot
                rectVertices[6] = rect.width;
                rectVertices[7] = 0;
                // left-bot
                rectVertices[8] = 0;
                rectVertices[9] = 0;
                collisionAreas.add(new CollisionArea(rect.x, rect.y, rectVertices, mapObj.getProperties().get("isWater", false, Boolean.class)));
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
                final GameObject gameObj = new GameObject((TiledMapTileMapObject) mapObj);
                gameObjects.add(gameObj);
                if (gameObj.getType() == GameObjectComponent.GameObjectType.CRYSTAL) {
                    ++numCrystals;
                }
            } else {
                Gdx.app.log(TAG, "Unsupported mapObject for objects layer: " + mapObj);
            }
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    Array<CollisionArea> getCollisionAreas() {
        return collisionAreas;
    }

    public Vector2 getStartLocation() {
        return startLocation;
    }

    public Array<Rectangle> getCamBoundaries() {
        return camBoundaries;
    }

    public int getNumCrystals() {
        return numCrystals;
    }
}
