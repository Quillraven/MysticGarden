package com.quillraven.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.Map;
import com.quillraven.game.core.EGameState;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.GameState;
import com.quillraven.game.core.InputController;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ui.GameUI;

public class GSGame extends GameState<GameUI> {
    private final ECSEngine ecsEngine;
    private final World world;
    private final Map map;

    public GSGame(final EGameState type, final Game game, final GameUI hud) {
        super(type, game, hud);
        map = new Map(game.getAssetManager().get("map/map.tmx", TiledMap.class));

        //box2d
        Box2D.init();
        world = new World(new Vector2(0, 0), true);

        this.ecsEngine = new ECSEngine(game, world, new OrthographicCamera(), map);
        //TODO add player start location information to map
        ecsEngine.addPlayer(32.5f, 27);
        map.createGameObjects(ecsEngine);

        /*TODO
         *) add collision layer to tiledmap and parse it
         *) add bounding restriction of map to PlayerCameraSystem (observer system for boundary area change)
         *) include animationCache instead of creating new animations all the time
         *) add player animation
         *) make a HUD at the bottom of the screen (game time, found equippment boxes, found crystals)
         */
    }

    @Override
    public void processInput(final InputController inputController) {
        ecsEngine.processInput(inputController);
    }

    @Override
    public void step(final float fixedTimeStep) {
        super.step(fixedTimeStep);
        // important to update entity engine before updating the box2d because we need to store
        // the body position before the next step for the interpolation rendering
        ecsEngine.update(fixedTimeStep);
        world.step(fixedTimeStep, 6, 2);
    }

    @Override
    public void render(final float alpha) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ecsEngine.render(alpha);
        hud.render();
    }

    @Override
    public void resize(final int width, final int height) {
        super.resize(width, height);
        ecsEngine.resize(width, height);
    }

    @Override
    public void dispose() {
        ecsEngine.dispose();
        world.dispose();
    }
}
