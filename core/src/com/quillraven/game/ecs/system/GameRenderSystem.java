package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.ecs.EntityEngine;
import com.quillraven.game.core.ecs.RenderSystem;
import com.quillraven.game.core.ecs.component.AnimationComponent;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.map.Map;
import com.quillraven.game.map.MapManager;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class GameRenderSystem implements RenderSystem, MapManager.MapListener {
    private static final String TAG = GameRenderSystem.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final int RENDER_OFFSET_Y = 4;

    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private Array<TiledMapTileLayer> layersToRender;
    private final Box2DDebugRenderer b2dRenderer;
    private final SpriteBatch spriteBatch;

    private final World world;
    private final OrthographicCamera gameCamera;
    private final Vector3 viewPortOffset;

    private final ImmutableArray<Entity> entitiesForRender;
    private final ComponentMapper<Box2DComponent> b2dCmpMapper;
    private final ComponentMapper<AnimationComponent> aniCmpMapper;

    public GameRenderSystem(final EntityEngine entityEngine, final World world, final OrthographicCamera gameCamera, final ComponentMapper<Box2DComponent> b2dCmpMapper, final ComponentMapper<AnimationComponent> aniCmpMapper) {
        this.b2dCmpMapper = b2dCmpMapper;
        this.aniCmpMapper = aniCmpMapper;
        entitiesForRender = entityEngine.getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class).get());
        this.spriteBatch = Utils.getSpriteBatch();
        mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, spriteBatch);
        b2dRenderer = DEBUG ? new Box2DDebugRenderer() : null;
        this.gameCamera = gameCamera;
        this.world = world;
        viewport = new FitViewport(9, 16, gameCamera);
        viewPortOffset = new Vector3();

        MapManager.INSTANCE.addMapListener(this);
    }

    @Override
    public void render(final float alpha) {
        viewport.apply();

        spriteBatch.begin();
        AnimatedTiledMapTile.updateAnimationBaseTime();
        if (mapRenderer.getMap() != null) {
            mapRenderer.setView(gameCamera);
            for (TiledMapTileLayer layer : layersToRender) {
                mapRenderer.renderTileLayer(layer);
            }
        }

        for (final Entity entity : entitiesForRender) {
            final AnimationComponent aniCmp = aniCmpMapper.get(entity);
            if (aniCmp.animation == null) {
                continue;
            }

            final Box2DComponent b2dCmp = b2dCmpMapper.get(entity);
            final Vector2 position = b2dCmp.body.getPosition();

            final Sprite frame = aniCmp.animation.getKeyFrame(aniCmp.aniTimer, true);
            frame.setColor(Color.WHITE);
            frame.setOriginCenter();
            frame.setBounds(MathUtils.lerp(b2dCmp.positionBeforeUpdate.x, position.x, alpha) - (aniCmp.width * 0.5f), MathUtils.lerp(b2dCmp.positionBeforeUpdate.y, position.y, alpha) - (b2dCmp.height * 0.5f), aniCmp.width, aniCmp.height);
            frame.draw(spriteBatch);
        }
        spriteBatch.end();

        if (DEBUG) {
            b2dRenderer.render(world, gameCamera.combined);
            Gdx.app.debug(TAG, "Last number of render calls: " + spriteBatch.renderCalls);
        }
    }

    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height, false);
        viewPortOffset.set(gameCamera.position.x - gameCamera.viewportWidth * 0.5f, RENDER_OFFSET_Y + gameCamera.position.y - gameCamera.viewportHeight * 0.5f, 0);
        gameCamera.project(viewPortOffset, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
        viewport.setScreenY((int) viewPortOffset.y);
    }

    @Override
    public void dispose() {
        if (b2dRenderer != null) {
            b2dRenderer.dispose();
        }
    }

    @Override
    public void mapChanged(final Map map) {
        mapRenderer.setMap(map.getTiledMap());
        layersToRender = map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class);
    }
}
