package com.quillraven.game.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.quillraven.game.MysticGarden;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = MysticGarden.V_WIDTH;
        config.height = MysticGarden.V_HEIGHT;
        config.title = MysticGarden.TITLE;
        new LwjglApplication(new MysticGarden(), config).setLogLevel(Application.LOG_DEBUG);
    }
}
