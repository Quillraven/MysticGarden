package com.quillraven.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.SkinLoader;
import com.quillraven.game.core.ui.TTFSkin;

import java.util.EnumMap;
import java.util.Map;

public class Game implements Disposable {
    private static final String TAG = Game.class.getSimpleName();

    private final AssetManager assetManager;
    private final TTFSkin skin;
    private final SpriteBatch spriteBatch;
    private final InputController inputController;
    private final AudioManager audioManager;
    private final I18NBundle i18NBundle;

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
        assetManager.setLoader(TTFSkin.class, new SkinLoader(resolver));
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));
        assetManager.load("hud/hud.json", TTFSkin.class, new SkinLoader.SkinParameter("hud/font.ttf", 16, 24, 48));
        assetManager.load(AudioManager.AudioType.INTRO.getFilePath(), Music.class);
        assetManager.load("i18n/strings", I18NBundle.class);
        assetManager.finishLoading();
        skin = assetManager.get("hud/hud.json", TTFSkin.class);
        i18NBundle = assetManager.get("i18n/strings", I18NBundle.class);

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

    InputController getInputController() {
        return inputController;
    }

    public TTFSkin getSkin() {
        return skin;
    }

    public I18NBundle getI18NBundle() {
        return i18NBundle;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setGameState(final EGameState gameStateType) {
        setGameState(gameStateType, false);
    }

    public void setGameState(final EGameState gameStateType, final boolean disposeActive) {
        if (activeState != null) {
            Gdx.app.debug(TAG, "Deactivating gamestate " + (disposeActive ? "and disposing" : "") + " " + activeState);
            activeState.deactivate();
            if (disposeActive) {
                gameStateCache.remove(activeState.getType());
                activeState.dispose();
            }
        }

        activeState = gameStateCache.get(gameStateType);
        if (activeState == null) {
            Gdx.app.debug(TAG, "Creating new gamestate: " + gameStateType);

            try {
                final HUD hud = (HUD) ClassReflection.getConstructor(gameStateType.getHUDType(), Game.class).newInstance(this);
                activeState = (GameState) ClassReflection.getConstructor(gameStateType.getGameStateType(), EGameState.class, Game.class, gameStateType.getHUDType()).newInstance(gameStateType, this, hud);
                gameStateCache.put(gameStateType, activeState);
            } catch (ReflectionException e) {
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
