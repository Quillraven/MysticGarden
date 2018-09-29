package com.quillraven.game.ecs.system;

import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.core.Game;

public class GameTimeSystem extends IntervalSystem {
    private float elapsedTime;
    private int seconds;
    private int minutes;
    private int hours;
    private final Array<GameTimeListener> listeners;

    public GameTimeSystem() {
        super(Game.TARGET_FRAME_TIME);
        elapsedTime = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
        listeners = new Array<>();
    }

    public void addGameTimeListener(final GameTimeListener listener) {
        listeners.add(listener);
    }

    @Override
    protected void updateInterval() {
        elapsedTime += Game.TARGET_FRAME_TIME;
        if (elapsedTime >= 1) {
            while (elapsedTime >= 1) {
                elapsedTime -= 1;
                ++seconds;
                if (seconds == 60) {
                    seconds = 0;
                    ++minutes;
                    if (minutes == 60) {
                        minutes = 0;
                        ++hours;
                    }
                }
            }
            setTime(hours, minutes, seconds);
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setTime(final int hours, final int minutes, final int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        for (final GameTimeListener listener : listeners) {
            listener.gameTimeUpdated(hours, minutes, seconds);
        }
    }

    public interface GameTimeListener {
        void gameTimeUpdated(final int hours, final int minutes, final int seconds);
    }
}
