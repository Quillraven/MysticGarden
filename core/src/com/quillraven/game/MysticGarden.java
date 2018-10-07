package com.quillraven.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.PreferenceManager;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.map.MapManager;


public class MysticGarden extends ApplicationAdapter {
    private static final String TAG = MysticGarden.class.getSimpleName();

    public static final String TITLE = "MysticGarden";

    public static final float UNIT_SCALE = 1 / 32f;
    public static final short BIT_PLAYER = 1 << 1;
    public static final short BIT_GAME_OBJECT = 1 << 2;
    public static final short BIT_WORLD = 1 << 3;
    public static final short BIT_WATER = 1 << 4;

    private ResourceManager resourceManager;
    private SpriteBatch spriteBatch;
    private WorldContactManager contactManager;
    private AudioManager audioManager;
    private PreferenceManager preferenceManager;
    private MapManager mapManager;
    private Game game;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        resourceManager = new ResourceManager();
        preferenceManager = new PreferenceManager();
        contactManager = new WorldContactManager();
        audioManager = new AudioManager();
        mapManager = new MapManager();

        spriteBatch = new SpriteBatch();
        this.game = new Game(EGameState.LOADING);
    }

    public WorldContactManager getWorldContactManager() {
        return contactManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public MapManager getMapManager() {
        return mapManager;
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
