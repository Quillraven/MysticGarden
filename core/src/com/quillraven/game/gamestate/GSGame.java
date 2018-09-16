package com.quillraven.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.core.EGameState;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.GameState;
import com.quillraven.game.core.InputController;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ui.GameUI;

public class GSGame extends GameState<GameUI> {
    private final ECSEngine ecsEngine;
    private final World world;

    public GSGame(final EGameState type, final Game game, final GameUI hud) {
        super(type, game, hud);

        this.ecsEngine = new ECSEngine(game);
        ecsEngine.addPlayer(32.5f, 27);
        this.world = game.getWorld();
    }

    @Override
    public void processInput(final InputController inputController) {
        ecsEngine.processInput(inputController);
    }

    @Override
    public void activate() {
        /*TODO
         *) add MapManager and map class for property parsing and game object creation
         *) render game objects
         *) add Box2D for collision
         *) add camera boundaries logic
         *) make a HUD at the bottom of the screen
         */

        super.activate();
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
    }
}
