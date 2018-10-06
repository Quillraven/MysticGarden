package com.quillraven.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferenceManager {
    private static final String TAG = PreferenceManager.class.getSimpleName();

    private final Preferences preferences;

    public PreferenceManager() {
        // creates new preference under %UserProfile%/.prefs
        preferences = Gdx.app.getPreferences("mysticGarden");
    }

    public boolean containsKey(final String key) {
        return preferences.contains(key);
    }

    public void setFloatValue(final String key, final float value) {
        Gdx.app.debug(TAG, "Setting float value " + value + " for key " + key);
        preferences.putFloat(key, value);
        preferences.flush();
    }

    public float getFloatValue(final String key) {
        return preferences.getFloat(key, 0f);
    }

    public void setStringValue(final String key, final String value) {
        Gdx.app.debug(TAG, "Setting string value " + value + " for key " + key);
        preferences.putString(key, value);
        preferences.flush();
    }

    public String getStringValue(final String key) {
        return preferences.getString(key, "");
    }

    public void removeKey(final String key) {
        preferences.remove(key);
        preferences.flush();
    }
}
