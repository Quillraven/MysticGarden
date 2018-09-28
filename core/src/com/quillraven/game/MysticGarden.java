package com.quillraven.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.gamestate.EGameState;


/*TODO
 *) add main menu screen to show credits and adjust sound volume
 *) add save/load to continue game where player left it
 *) add a "story info table" to GameUI that is shown until map is loaded + 3 seconds
 *) add victory gamestate to show beloved couple and score ;)
 *) finalize map and remaining game logic
 *) add particle effects to some areas of the map (f.e. torches and fire stones)
 */
public class MysticGarden extends ApplicationAdapter {
    private static final String TAG = MysticGarden.class.getSimpleName();

    public static final int V_WIDTH = 450;
    public static final int V_HEIGHT = 800;
    public static final String TITLE = "MysticGarden";

    public static final float UNIT_SCALE = 1 / 32f;
    public static final short BIT_PLAYER = 1 << 1;
    public static final short BIT_GAME_OBJECT = 1 << 2;
    public static final short BIT_GROUND = 1 << 3;
    public static final short BIT_WATER = 1 << 4;

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
