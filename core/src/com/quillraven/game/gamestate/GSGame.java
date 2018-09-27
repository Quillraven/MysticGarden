package com.quillraven.game.gamestate;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.WorldContactManager;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.core.gamestate.GameState;
import com.quillraven.game.core.input.EKey;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.system.PlayerContactSystem;
import com.quillraven.game.ecs.system.PlayerMovementSystem;
import com.quillraven.game.map.MapManager;
import com.quillraven.game.ui.GameUI;

import static com.quillraven.game.MysticGarden.*;

public class GSGame extends GameState<GameUI> implements PlayerContactSystem.PlayerContactListener {
    private final ECSEngine ecsEngine;
    private final RayHandler rayHandler;
    private final Color ambientLightColor;
    private final World world;
    private float elapsedTime;

    private int seconds;
    private int minutes;
    private int hours;

    public GSGame(final EGameState type, final HUD hud) {
        super(type, hud);
        elapsedTime = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;

        // box2d
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(WorldContactManager.INSTANCE);
        rayHandler = new RayHandler(world);
        ambientLightColor = new Color(0, 0, 0, 0.05f);
        rayHandler.setAmbientLight(ambientLightColor);
        // player light should not collide with water because water should not throw shadows
        Light.setGlobalContactFilter(BIT_PLAYER, (short) 1, (short) (BIT_GROUND | BIT_GAME_OBJECT));

        // entity component system
        this.ecsEngine = new ECSEngine(world, rayHandler, new OrthographicCamera());
        ecsEngine.getSystem(PlayerContactSystem.class).addPlayerContactListener(this);

        // init map -> this needs to happen after ECSEngine creation because some systems need to register as listeners first
        MapManager.INSTANCE.loadMap();
        MapManager.INSTANCE.spawnGameObjects(ecsEngine);
        MapManager.INSTANCE.spawnCollisionAreas(world);
        ecsEngine.addPlayer(MapManager.INSTANCE.getCurrentMap().getStartLocation());

        AudioManager.INSTANCE.playAudio(AudioManager.AudioType.ALMOST_FINISHED);
    }

    @Override
    protected GameUI createHUD(final HUD hud, final TTFSkin skin) {
        return new GameUI(hud, skin);
    }

    @Override
    public void activate() {
        super.activate();
        InputManager.INSTANCE.addKeyInputListener(ecsEngine.getSystem(PlayerMovementSystem.class));
    }

    @Override
    public void deactivate() {
        super.deactivate();
        InputManager.INSTANCE.removeKeyInputListener(ecsEngine.getSystem(PlayerMovementSystem.class));
    }

    @Override
    public void step(final float fixedTimeStep) {
        elapsedTime += fixedTimeStep;
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
            gameStateHUD.setGameTime(hours, minutes, seconds);
        }
        // important to update entity engine before updating the box2d because we need to store
        // the body position before the next step for the interpolation rendering
        ecsEngine.update(fixedTimeStep);
        world.step(fixedTimeStep, 6, 2);
        super.step(fixedTimeStep);
    }

    @Override
    public void render(final float alpha) {
        ecsEngine.render(alpha);
        super.render(alpha);
    }

    @Override
    public void resize(final int width, final int height) {
        ecsEngine.resize(width, height);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        ecsEngine.dispose();
        world.dispose();
        rayHandler.dispose();
    }

    @Override
    public void keyDown(final InputManager manager, final EKey key) {
        // input handling is done within ECS systems
    }

    @Override
    public void keyUp(final InputManager manager, final EKey key) {
        // input handling is done within ECS systems
    }

    @Override
    public void crystalContact(final int crystalsFound) {
        gameStateHUD.setCrystals(crystalsFound);
        if (crystalsFound == 1) {
            gameStateHUD.showInfoMessage(hud.getLocalizedString("crystalInfo"), 7.0f);
        }
    }

    @Override
    public void itemContact(final GameObjectComponent.GameObjectType type) {
        if (type == GameObjectComponent.GameObjectType.AXE) {
            gameStateHUD.setAxe(true);
            gameStateHUD.showInfoMessage(hud.getLocalizedString("axeInfo"), 7.0f);
        }
    }

    @Override
    public void chromaOrbContact(final int chromaOrbsFound) {
        ambientLightColor.a += 0.05f;
        rayHandler.setAmbientLight(ambientLightColor);
        gameStateHUD.setChromaOrb(chromaOrbsFound);
        if (chromaOrbsFound == 1) {
            gameStateHUD.showInfoMessage(hud.getLocalizedString("chromaOrbInfo"), 7.0f);
        }
    }
}
