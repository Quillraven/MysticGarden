package com.quillraven.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.quillraven.game.MysticGarden;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(MysticGarden.TITLE);
        config.setWindowedMode(450, 800);
        new Lwjgl3Application(new MysticGarden(), config);
    }
}
