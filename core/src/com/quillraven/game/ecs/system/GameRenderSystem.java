package com.quillraven.game.ecs.system;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.quillraven.game.core.ecs.component.ParticleEffectComponent;
import com.quillraven.game.core.ecs.component.RemoveComponent;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.map.Map;
import com.quillraven.game.map.MapManager;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class GameRenderSystem implements RenderSystem, MapManager.MapListener {
    private static final String TAG = GameRenderSystem.class.getSimpleName();
    private static final boolean DEBUG = false;

    static final int RENDER_OFFSET_Y = 4;

    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private Array<TiledMapTileLayer> layersToRender;
    private final Box2DDebugRenderer b2dRenderer;
    private final SpriteBatch spriteBatch;

    private final World world;
    private final RayHandler rayHandler;
    private final OrthographicCamera gameCamera;
    private final Vector3 renderOffsetVector;

    private final ImmutableArray<Entity> gameObjectsForRender;
    private final ImmutableArray<Entity> charactersForRender;
    private final ImmutableArray<Entity> particleEffectsForRender;
    private final ComponentMapper<Box2DComponent> b2dCmpMapper;
    private final ComponentMapper<AnimationComponent> aniCmpMapper;
    private final ComponentMapper<ParticleEffectComponent> peCmpMapper;

    public GameRenderSystem(final EntityEngine entityEngine, final World world, final RayHandler rayHandler, final OrthographicCamera gameCamera, final ComponentMapper<Box2DComponent> b2dCmpMapper, final ComponentMapper<AnimationComponent> aniCmpMapper, final ComponentMapper<ParticleEffectComponent> peCmpMapper) {
        this.b2dCmpMapper = b2dCmpMapper;
        this.aniCmpMapper = aniCmpMapper;
        this.peCmpMapper = peCmpMapper;
        gameObjectsForRender = entityEngine.getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class, GameObjectComponent.class).exclude(RemoveComponent.class).get());
        charactersForRender = entityEngine.getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class, PlayerComponent.class).exclude(RemoveComponent.class).get());
        particleEffectsForRender = entityEngine.getEntitiesFor(Family.all(ParticleEffectComponent.class).exclude(RemoveComponent.class).get());
        this.spriteBatch = Utils.getSpriteBatch();
        mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, spriteBatch);
        b2dRenderer = DEBUG ? new Box2DDebugRenderer() : null;
        this.gameCamera = gameCamera;
        this.world = world;
        this.rayHandler = rayHandler;
        viewport = new FitViewport(9, 16, gameCamera);
        renderOffsetVector = new Vector3();

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

        // render game objects first because they are in the same texture atlas as the map so we avoid a texture binding --> better performance
        for (final Entity entity : gameObjectsForRender) {
            renderEntity(entity, alpha);
        }
        for (final Entity entity : charactersForRender) {
            renderEntity(entity, alpha);
        }

        // render particle effects
        for (final Entity entity : particleEffectsForRender) {
            final ParticleEffectComponent peCmp = peCmpMapper.get(entity);
            if (peCmp.effect != null) {
                peCmp.effect.draw(spriteBatch);
            }
        }
        // we need to manually reset the blend function because all effects have setEmittersCleanUpBlendFunction set to false
        // to increase render performance (refer to ParticleEffectManager)
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatch.end();

        // draw lights
        rayHandler.setCombinedMatrix(gameCamera);
        rayHandler.updateAndRender();
        if (DEBUG) {
            b2dRenderer.render(world, gameCamera.combined);
            Gdx.app.debug(TAG, "Last number of render calls: " + spriteBatch.renderCalls);
        }
    }

    private void renderEntity(final Entity entity, final float alpha) {
        final AnimationComponent aniCmp = aniCmpMapper.get(entity);
        if (aniCmp.animation == null) {
            return;
        }

        final Box2DComponent b2dCmp = b2dCmpMapper.get(entity);
        final Vector2 position = b2dCmp.body.getPosition();

        final Sprite frame = aniCmp.animation.getKeyFrame(aniCmp.aniTimer, true);
        frame.setColor(Color.WHITE);
        frame.setOriginCenter();
        frame.setBounds(MathUtils.lerp(b2dCmp.positionBeforeUpdate.x, position.x, alpha) - (aniCmp.width * 0.5f), MathUtils.lerp(b2dCmp.positionBeforeUpdate.y, position.y, alpha) - (b2dCmp.height * 0.5f), aniCmp.width, aniCmp.height);
        frame.draw(spriteBatch);
    }

    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height, false);
        // offset viewport by y-axis (get distance from viewport to viewport with offset)
        renderOffsetVector.set(gameCamera.position.x - gameCamera.viewportWidth * 0.5f, RENDER_OFFSET_Y + gameCamera.position.y - gameCamera.viewportHeight * 0.5f, 0);
        gameCamera.project(renderOffsetVector, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
        viewport.setScreenY((int) renderOffsetVector.y);

        rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
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
