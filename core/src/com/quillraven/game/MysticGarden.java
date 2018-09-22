package com.quillraven.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.quillraven.game.core.EGameState;
import com.quillraven.game.core.Game;


/*TODO
 *) make world contact listener and add logic to collide with crystals/game objects (also update hud accordingly)
 *) add logic that when player has an axe then he can cut some trees (also play sound when a tree is cut)
 *) add box2d light to make the player only see a little circle that fluctuates
 *) make a better solution for all the parameter passing (singletons? CreateContext class? ObserverPattern for inputProcessing?)
 *) add main menu screen to show credits and adjust sound volume
 *) add save/load to continue game where player left it
 *) finalize map and remaining game logic
 *) add particle effects to some areas of the map (f.e. torches and fire stones)
 *)
 */
public class MysticGarden extends ApplicationAdapter {
    public static final float UNIT_SCALE = 1 / 32f;

    private Game game;

    @Override
    public void create() {
        this.game = new Game(EGameState.LOADING);
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
    }
}
