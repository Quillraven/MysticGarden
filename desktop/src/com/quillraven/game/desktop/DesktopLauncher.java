package com.quillraven.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.quillraven.game.MysticGarden;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 450;
        config.height = 800;
        config.title = MysticGarden.TITLE;
        new LwjglApplication(new MysticGarden(), config);
    }
}
