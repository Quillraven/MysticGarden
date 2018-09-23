package com.quillraven.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.ecs.ECSEngine;

public enum MapManager {
    INSTANCE;

    private static final String TAG = MapManager.class.getSimpleName();

    private Map currentMap;
    private final ResourceManager resourceManager;
    private final IntMap<Animation<Sprite>> gameObjAnimationCache;
    private final Array<MapListener> mapListeners;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    MapManager() {
        this.currentMap = null;
        this.resourceManager = Utils.getResourceManager();
        this.gameObjAnimationCache = new IntMap<>();
        this.mapListeners = new Array<>();
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
    }

    public void addMapListener(final MapListener mapListener) {
        mapListeners.add(mapListener);
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public void loadMap() {
        if (currentMap == null) {
            currentMap = new Map(resourceManager.get("map/map.tmx", TiledMap.class));
        }
        for (final MapListener mapListener : mapListeners) {
            mapListener.mapChanged(currentMap);
        }
    }

    public void spawnGameObjects(final ECSEngine ecsEngine) {
        //TODO remove old gameobjects in ECSEngine
        if (currentMap == null) {
            Gdx.app.error(TAG, "Cannot spawn game objects of null map");
            return;
        }

        for (final GameObject gameObj : currentMap.getGameObjects()) {
            ecsEngine.addGameObject(gameObj.getBoundaries(), getAnimation(gameObj));
        }
    }

    public void spawnCollisionAreas(final World world) {
        //TODO remove old collision areas
        if (currentMap == null) {
            Gdx.app.error(TAG, "Cannot spawn collision areas of null map");
            return;
        }

        for (final CollisionArea collArea : currentMap.getCollisionAreas()) {
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(collArea.getStartLocation());
            final Body body = world.createBody(bodyDef);
            final ChainShape shape = new ChainShape();
            shape.createChain(collArea.getVertices());
            fixtureDef.shape = shape;
            fixtureDef.friction = 0;
            fixtureDef.isSensor = false;
            body.createFixture(fixtureDef);
            shape.dispose();
        }
    }

    private Animation<Sprite> getAnimation(final GameObject gameObj) {
        final TiledMapTile tile = gameObj.getTile();
        Animation<Sprite> animation = gameObjAnimationCache.get(tile.getId());
        if (animation == null) {
            if (tile instanceof AnimatedTiledMapTile) {
                final AnimatedTiledMapTile aniTile = (AnimatedTiledMapTile) tile;
                final Array<Sprite> keyFrames = new Array<>();
                for (final StaticTiledMapTile staticTile : aniTile.getFrameTiles()) {
                    keyFrames.add(new Sprite(staticTile.getTextureRegion()));
                }
                animation = new Animation<>(gameObj.getAnimationInterval(), keyFrames, Animation.PlayMode.LOOP);
                gameObjAnimationCache.put(tile.getId(), animation);
            } else if (tile instanceof StaticTiledMapTile) {
                animation = new Animation<>(0, new Sprite(tile.getTextureRegion()));
                gameObjAnimationCache.put(tile.getId(), animation);
            } else {
                Gdx.app.error(TAG, "Unsupported TiledMapTile type " + tile);
            }
        }
        return animation;
    }

    public interface MapListener {
        void mapChanged(final Map map);
    }
}
