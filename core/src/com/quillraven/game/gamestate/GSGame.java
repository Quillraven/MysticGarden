package com.quillraven.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.core.*;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.map.MapManager;
import com.quillraven.game.ui.GameUI;

public class GSGame extends GameState<GameUI> {
    private final ECSEngine ecsEngine;
    private final World world;

    public GSGame(final EGameState type, final Game game, final GameUI hud) {
        super(type, game, hud);

        // box2d
        Box2D.init();
        world = new World(new Vector2(0, 0), true);

        // entity component system
        final MapManager mapManager = new MapManager(game.getAssetManager());
        this.ecsEngine = new ECSEngine(game, world, new OrthographicCamera(), mapManager);

        // init map -> this needs to happen after ECSEngine creation because some systems need to register as listeners first
        mapManager.loadMap();
        ecsEngine.addPlayer(mapManager.getCurrentMap().getStartLocation());
        mapManager.spawnGameObjects(ecsEngine);
        mapManager.spawnCollisionAreas(world);

        game.getAudioManager().playAudio(AudioManager.AudioType.ALMOST_FINISHED);

        /*TODO
         *) fix rendering (viewport should be 4 worldunits up and PlayerCameraSystem should not get messed up if boundaries are smaller than the camera view
         *) make a HUD at the bottom of the screen (game time, found equippment boxes, found crystals)
         *) make a better solution for all the parameter passing (singletons? CreateContext class? ObserverPattern for inputProcessing?)
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
