package com.quillraven.game.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.input.EKey;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.core.input.KeyInputListener;

import static com.quillraven.game.core.Game.TARGET_FRAME_TIME;

public class HUD extends InputListener implements Disposable, KeyInputListener {
    private static final String TAG = HUD.class.getSimpleName();

    private final Stage stage;
    private final TTFSkin skin;
    private final I18NBundle i18NBundle;
    private final Stack gameStateHUDs;
    private final OnScreenUI onScreenUI;

    public HUD() {
        stage = new Stage(new FitViewport(450, 800, new OrthographicCamera()), Utils.getSpriteBatch());
        gameStateHUDs = new Stack();
        gameStateHUDs.setFillParent(true);
        this.stage.addActor(gameStateHUDs);

        final ResourceManager resourceManager = Utils.getResourceManager();
        resourceManager.load("i18n/strings", I18NBundle.class);
        skin = resourceManager.loadSkinSynchronously("hud/hud.json", "hud/font.ttf", 16, 20, 26, 32);
        this.i18NBundle = resourceManager.get("i18n/strings", I18NBundle.class);

        onScreenUI = new OnScreenUI(this, skin);
        gameStateHUDs.add(onScreenUI);
        stage.addListener(this);
    }

    public Stage getStage() {
        return stage;
    }

    public TTFSkin getSkin() {
        return skin;
    }

    public String getLocalizedString(final String key) {
        return i18NBundle.format(key);
    }

    public void addGameStateHUD(final Table table) {
        gameStateHUDs.add(table);
        onScreenUI.toFront();
    }

    public void removeGameStateHUD(final Table table) {
        gameStateHUDs.removeActor(table);
    }

    public void step(final float deltaTime) {
        stage.act(deltaTime);
    }

    public void render(final float alpha) {
        stage.act(alpha * TARGET_FRAME_TIME);
        stage.getViewport().apply();
        stage.draw();
    }

    public void resize(final int width, final int height) {
        Gdx.app.debug(TAG, "Resizing HUD to " + width + "x" + height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        final EKey relatedKey = onScreenUI.getRelatedKey(event.getListenerActor());
        if (relatedKey != null) {
            Utils.getInputManager().notifyKeyDown(relatedKey);
            return true;
        }
        return false;
    }

    @Override
    public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        final EKey relatedKey = onScreenUI.getRelatedKey(event.getListenerActor());
        if (relatedKey != null) {
            Utils.getInputManager().notifyKeyUp(relatedKey);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing HUD");
        stage.dispose();
    }

    @Override
    public void keyDown(final InputManager manager, final EKey key) {
        onScreenUI.setChecked(key, true);
    }

    @Override
    public void keyUp(final InputManager manager, final EKey key) {
        onScreenUI.setChecked(key, false);
    }
}
