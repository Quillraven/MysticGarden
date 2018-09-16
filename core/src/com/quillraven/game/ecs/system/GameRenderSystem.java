package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.quillraven.game.Map;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ecs.EntityEngine;
import com.quillraven.game.core.ecs.RenderSystem;
import com.quillraven.game.core.ecs.component.AnimationComponent;
import com.quillraven.game.core.ecs.component.Box2DComponent;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class GameRenderSystem implements RenderSystem {
    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer b2dRenderer;

    private final World world;
    private final OrthographicCamera gameCamera;

    private final ImmutableArray<Entity> entitiesForRender;
    private final ComponentMapper<Box2DComponent> b2dCmpMapper;
    private final ComponentMapper<AnimationComponent> aniCmpMapper;

    public GameRenderSystem(final EntityEngine entityEngine, final Game game, final World world, final OrthographicCamera gameCamera, final Map map, final ComponentMapper<Box2DComponent> b2dCmpMapper, final ComponentMapper<AnimationComponent> aniCmpMapper) {
        this.b2dCmpMapper = b2dCmpMapper;
        this.aniCmpMapper = aniCmpMapper;
        entitiesForRender = entityEngine.getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class).get());

        mapRenderer = new OrthogonalTiledMapRenderer(map.getTiledMap(), UNIT_SCALE, game.getSpriteBatch());
        b2dRenderer = new Box2DDebugRenderer();
        this.gameCamera = gameCamera;
        this.world = world;
        viewport = new FitViewport(9, 16, gameCamera);
    }

    @Override
    public void render(final float alpha) {
        viewport.apply();

        float width = gameCamera.viewportWidth * gameCamera.zoom;
        float height = gameCamera.viewportHeight * gameCamera.zoom;
        float w = width * Math.abs(gameCamera.up.y) + height * Math.abs(gameCamera.up.x);
        float h = height * Math.abs(gameCamera.up.y) + width * Math.abs(gameCamera.up.x);
        mapRenderer.setView(gameCamera.combined, gameCamera.position.x - w * 0.5f, gameCamera.position.y - h * 0.5f, w, h);
        mapRenderer.render();

        mapRenderer.getBatch().begin();
        final float invertAlpha = 1.0f - alpha;
        for (final Entity entity : entitiesForRender) {
            final Box2DComponent b2dCmp = b2dCmpMapper.get(entity);
            final Vector2 position = b2dCmp.body.getPosition();
            final AnimationComponent aniCmp = aniCmpMapper.get(entity);

            // calculate interpolated position for rendering
            final float x = (position.x * alpha + b2dCmp.positionBeforeUpdate.x * invertAlpha) - (b2dCmp.width * 0.5f);
            final float y = (position.y * alpha + b2dCmp.positionBeforeUpdate.y * invertAlpha) - (b2dCmp.height * 0.5f);

            final Sprite frame = aniCmp.animation.getKeyFrame(aniCmp.aniTimer, true);
            frame.setColor(Color.WHITE);
            frame.setOriginCenter();
            frame.setBounds(x, y, aniCmp.width, aniCmp.height);
            frame.draw(mapRenderer.getBatch());
        }
        mapRenderer.getBatch().end();

        b2dRenderer.render(world, gameCamera.combined);
    }

    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void dispose() {
        b2dRenderer.dispose();
    }
}
