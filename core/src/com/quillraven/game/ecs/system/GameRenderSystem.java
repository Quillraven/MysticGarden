package com.quillraven.game.ecs.system;

import box2dLight.RayHandler;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
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
import com.quillraven.game.ecs.ECSEngine;
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

    private final Vector3 tmpVec3;
    private final Rectangle scissors;
    private final Rectangle clipBounds;
    private int lightScissorHeight;

    private final ImmutableArray<Entity> gameObjectsForRender;
    private final ImmutableArray<Entity> charactersForRender;
    private final ImmutableArray<Entity> particleEffectsForRender;

    public GameRenderSystem(final EntityEngine entityEngine, final World world, final RayHandler rayHandler, final OrthographicCamera gameCamera) {
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

        tmpVec3 = new Vector3();
        scissors = new Rectangle();
        clipBounds = new Rectangle();

        Utils.getMapManager().addMapListener(this);
    }

    @Override
    public void render(final float alpha) {
        viewport.apply();

        clipBounds.set(gameCamera.position.x - gameCamera.viewportWidth * 0.5f, gameCamera.position.y - gameCamera.viewportHeight * 0.5f, 9, 12);
        ScissorStack.calculateScissors(gameCamera, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight(), spriteBatch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);

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
            final ParticleEffectComponent peCmp = ECSEngine.peCmpMapper.get(entity);
            if (peCmp.effect != null) {
                peCmp.effect.draw(spriteBatch);
            }
        }
        // we need to manually reset the blend function because all effects have setEmittersCleanUpBlendFunction set to false
        // to increase render performance (refer to ParticleEffectManager)
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatch.end();
        ScissorStack.popScissors();

        // draw lights
        scissors.set(0, 0, Gdx.graphics.getWidth(), lightScissorHeight);
        ScissorStack.pushScissors(scissors);
        rayHandler.setCombinedMatrix(gameCamera);
        rayHandler.updateAndRender();
        if (DEBUG) {
            b2dRenderer.render(world, gameCamera.combined);
            Gdx.app.debug(TAG, "Last number of render calls: " + spriteBatch.renderCalls);
        }
        ScissorStack.popScissors();
    }

    private void renderEntity(final Entity entity, final float alpha) {
        final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
        if (aniCmp.animation == null) {
            return;
        }

        final Box2DComponent b2dCmp = ECSEngine.b2dCmpMapper.get(entity);
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

        // get bottom left corner + RENDER_OFFSET_Y position and convert it to screen coordinates to get the height offset in pixels for the viewport
        tmpVec3.set(gameCamera.position.x - gameCamera.viewportWidth * 0.5f, RENDER_OFFSET_Y + gameCamera.position.y - gameCamera.viewportHeight * 0.5f, 0);
        gameCamera.project(tmpVec3, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
        viewport.setScreenY((int) (1 + tmpVec3.y));

        // get top right corner of the game world which is still rendered to find out the maximum height that is used for rendering
        gameCamera.project(tmpVec3.set(gameCamera.position.x + gameCamera.viewportWidth * 0.5f, gameCamera.position.y - 4 + gameCamera.viewportHeight * 0.5f, 0), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
        lightScissorHeight = (int) (tmpVec3.y + 1);

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
