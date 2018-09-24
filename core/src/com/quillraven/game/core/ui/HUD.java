package com.quillraven.game.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.Utils;

import static com.quillraven.game.core.Game.TARGET_FRAME_TIME;

public class HUD implements Disposable {
    private static final String TAG = HUD.class.getSimpleName();

    private final Stage stage;
    private final TTFSkin skin;
    private final I18NBundle i18NBundle;
    private final Stack gameStateHUDs;

    public HUD() {
        stage = new Stage(new ScreenViewport(new OrthographicCamera(0, 0)), Utils.getSpriteBatch());
        gameStateHUDs = new Stack();
        gameStateHUDs.setFillParent(true);
        this.stage.addActor(gameStateHUDs);

        final ResourceManager resourceManager = Utils.getResourceManager();
        resourceManager.load("i18n/strings", I18NBundle.class);
        skin = resourceManager.loadSkinSynchronously("hud/hud.json", "hud/font.ttf", 16, 24, 48);
        this.i18NBundle = resourceManager.get("i18n/strings", I18NBundle.class);
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
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing HUD");
        stage.dispose();
    }

}
