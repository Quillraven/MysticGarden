package com.quillraven.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.Skin;
import com.quillraven.game.core.ui.SkinLoader;

import java.util.EnumMap;
import java.util.Map;

public class Game implements Disposable {
    private static final String TAG = Game.class.getSimpleName();

    private final AssetManager assetManager;
    private final Skin skin;
    private final SpriteBatch spriteBatch;
    private final InputController inputController;
    private final AudioManager audioManager;

    private final EnumMap<EGameState, GameState> gameStateCache;
    private GameState activeState;

    private float accumulator;

    public Game(final EGameState initialState) {
        spriteBatch = new SpriteBatch();
        gameStateCache = new EnumMap<>(EGameState.class);
        accumulator = 0;

        // setup assetmanager and skin
        final FileHandleResolver resolver = new InternalFileHandleResolver();
        this.assetManager = new AssetManager();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        assetManager.setLoader(Skin.class, new SkinLoader(resolver));
        assetManager.load("hud/hud.json", Skin.class, new SkinLoader.SkinParameter("hud/font.ttf", 16, 24, 48));
        assetManager.finishLoading();
        skin = assetManager.get("hud/hud.json", Skin.class);

        // setup inputlistener
        inputController = new InputController();
        Gdx.input.setInputProcessor(inputController);

        audioManager = new AudioManager(assetManager);

        setGameState(initialState);
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public InputController getInputController() {
        return inputController;
    }

    public Skin getSkin() {
        return skin;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setGameState(final EGameState gameStateType) {
        if (activeState != null) {
            Gdx.app.debug(TAG, "Deactivating gamestate " + activeState + "\n");
            activeState.deactivate();
        }

        activeState = gameStateCache.get(gameStateType);
        if (activeState == null) {
            Gdx.app.debug(TAG, "Creating new gamestate: " + gameStateType);

            try {
                final HUD hud = gameStateType.getHUDType().getConstructor(Game.class).newInstance(this);
                activeState = gameStateType.getGameStateType().getConstructor(Game.class, gameStateType.getHUDType()).newInstance(this, hud);
                gameStateCache.put(gameStateType, activeState);
            } catch (Exception e) {
                throw new GdxRuntimeException("Could not create gamestate of type " + gameStateType, e);
            }
        }

        Gdx.app.debug(TAG, "Activating gamestate " + activeState);
        activeState.activate();
        activeState.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void process() {
        final float deltaTime = Gdx.graphics.getRawDeltaTime();
        final float fixedTimeStep = 1 / 60.0f;
        accumulator += deltaTime > 0.25f ? 0.25f : deltaTime;

        activeState.processInput(inputController);
        activeState.render(accumulator / fixedTimeStep);
        while (accumulator >= fixedTimeStep) {
            activeState.step(fixedTimeStep);
            accumulator -= fixedTimeStep;
        }
    }

    public void resize(final int width, final int height) {
        activeState.resize(width, height);
    }

    @Override
    public void dispose() {
        for (final Map.Entry<EGameState, GameState> entry : gameStateCache.entrySet()) {
            Gdx.app.debug(TAG, "Disposing gamestate " + entry.getKey());
            entry.getValue().dispose();
        }
        Gdx.app.debug(TAG, "Maximum sprites in batch: " + spriteBatch.maxSpritesInBatch);
        Gdx.app.debug(TAG, "Last number of render calls: " + spriteBatch.renderCalls);
        spriteBatch.dispose();
        assetManager.dispose();
    }
}
