package com.quillraven.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.gamestate.EGameState;


/*TODO
 *) make world contact listener and add logic to collide with crystals/game objects (also update hud accordingly)
 *) add logic that when player has an axe then he can cut some trees (also play sound when a tree is cut)
 *) add box2d light to make the player only see a little circle that fluctuates
 *) add main menu screen to show credits and adjust sound volume
 *) add save/load to continue game where player left it
 *) finalize map and remaining game logic
 *) add particle effects to some areas of the map (f.e. torches and fire stones)
 *)
 */
public class MysticGarden extends ApplicationAdapter {
    private static final String TAG = MysticGarden.class.getSimpleName();
    public static final float UNIT_SCALE = 1 / 32f;

    private ResourceManager resourceManager;
    private SpriteBatch spriteBatch;
    private Game game;

    @Override
    public void create() {
        resourceManager = new ResourceManager();
        spriteBatch = new SpriteBatch();
        this.game = new Game(EGameState.LOADING);
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void render() {
        game.process();
    }

    @Override
    public void resize(final int width, final int height) {
        game.resize(width, height);
    }

    @Override
    public void dispose() {
        game.dispose();
        Gdx.app.debug(TAG, "Maximum sprites in batch: " + spriteBatch.maxSpritesInBatch);
        spriteBatch.dispose();
        resourceManager.dispose();
    }
}
