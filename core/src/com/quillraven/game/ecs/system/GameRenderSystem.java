package com.quillraven.game.ecs.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ecs.EntityEngine;
import com.quillraven.game.core.ecs.RenderSystem;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class GameRenderSystem extends RenderSystem {
    private final Viewport viewport;
    private final TiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer b2dRenderer;

    private final World world;
    private final OrthographicCamera gameCamera;

    public GameRenderSystem(final EntityEngine entityEngine, final Game game) {
        super(entityEngine);

        mapRenderer = new OrthogonalTiledMapRenderer(game.getAssetManager().get("map/map.tmx", TiledMap.class), UNIT_SCALE, game.getSpriteBatch());
        b2dRenderer = new Box2DDebugRenderer();
        gameCamera = game.getGameCamera();
        world = game.getWorld();
        viewport = new FitViewport(9, 16, gameCamera);

        viewport.getCamera().position.set(32.5f, 27, 0);
        viewport.getCamera().update();
    }

    @Override
    public void render(final float alpha) {
        viewport.apply();

        float width = gameCamera.viewportWidth * gameCamera.zoom;
        float height = gameCamera.viewportHeight * gameCamera.zoom;
        float w = width * Math.abs(gameCamera.up.y) + height * Math.abs(gameCamera.up.x);
        float h = height * Math.abs(gameCamera.up.y) + width * Math.abs(gameCamera.up.x);
        // clip bottom 4 world units for hud
        mapRenderer.setView(gameCamera.combined, gameCamera.position.x - w * 0.5f, 4 + gameCamera.position.y - h * 0.5f, w, h);
        mapRenderer.render();
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
