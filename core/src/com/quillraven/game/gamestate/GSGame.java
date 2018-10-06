package com.quillraven.game.gamestate;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.SaveState;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.core.gamestate.GameState;
import com.quillraven.game.core.input.EKey;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.ecs.system.GameTimeSystem;
import com.quillraven.game.ecs.system.PlayerContactSystem;
import com.quillraven.game.ecs.system.PlayerMovementSystem;
import com.quillraven.game.ui.GameUI;

import static com.quillraven.game.MysticGarden.*;

public class GSGame extends GameState<GameUI> implements PlayerContactSystem.PlayerContactListener, GameTimeSystem.GameTimeListener {
    private final ECSEngine ecsEngine;
    private final ImmutableArray<Entity> playerEntities;

    private final RayHandler rayHandler;
    private final World world;

    private final SaveState saveState;

    public GSGame(final EGameState type, final HUD hud) {
        super(type, hud);
        saveState = new SaveState();

        // box2d
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(Utils.getWorldContactManager());
        rayHandler = new RayHandler(world);
        // player light should not collide with water because water should not throw shadows
        Light.setGlobalContactFilter(BIT_PLAYER, (short) 1, (short) (BIT_WORLD | BIT_GAME_OBJECT));

        // entity component system
        this.ecsEngine = new ECSEngine(world, rayHandler, new OrthographicCamera());
        ecsEngine.getSystem(PlayerContactSystem.class).addPlayerContactListener(this);
        ecsEngine.getSystem(GameTimeSystem.class).addGameTimeListener(this);
        playerEntities = ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class).get());

        // init map -> this needs to happen after ECSEngine creation because some systems need to register as listeners first
        Utils.getMapManager().loadMap(world);
        ecsEngine.addPlayer(Utils.getMapManager().getCurrentMap().getStartLocation());
    }

    @Override
    protected GameUI createHUD(final HUD hud, final TTFSkin skin) {
        return new GameUI(hud, skin);
    }

    @Override
    public void activate() {
        super.activate();
        Utils.getInputManager().addKeyInputListener(ecsEngine.getSystem(PlayerMovementSystem.class));
        saveState.loadState(playerEntities.first(), ecsEngine, gameStateHUD);
        Utils.getAudioManager().playAudio(AudioManager.AudioType.ALMOST_FINISHED);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Utils.getInputManager().removeKeyInputListener(ecsEngine.getSystem(PlayerMovementSystem.class));
        saveState.updateState(playerEntities.first(), ecsEngine);
    }

    @Override
    public void step(final float fixedTimeStep) {
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
        if (key == EKey.BACK) {
            Utils.setGameState(EGameState.MENU);
        }
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
        switch (type) {
            case AXE:
                gameStateHUD.setAxe(true);
                gameStateHUD.showInfoMessage(hud.getLocalizedString("axeInfo"), 7.0f);
                break;
            case CLUB:
                gameStateHUD.setClub(true);
                gameStateHUD.showInfoMessage(hud.getLocalizedString("clubInfo"), 7.0f);
                break;
            case WAND:
                gameStateHUD.setWand(true);
                gameStateHUD.showInfoMessage(hud.getLocalizedString("wandInfo"), 7.0f);
                break;
            default:
                // nothing to do
                break;
        }
    }

    @Override
    public void chromaOrbContact(final int chromaOrbsFound) {
        gameStateHUD.setChromaOrb(chromaOrbsFound);
        if (chromaOrbsFound == 1) {
            gameStateHUD.showInfoMessage(hud.getLocalizedString("chromaOrbInfo"), 7.0f);
        }
    }

    @Override
    public void portalContact(final boolean hasAllCrystals) {
        if (hasAllCrystals) {
            Utils.setGameState(EGameState.VICTORY);
        } else {
            gameStateHUD.showInfoMessage(hud.getLocalizedString("portalInfo"), 5.0f);
        }
    }

    @Override
    public void gameTimeUpdated(final int hours, final int minutes, final int seconds) {
        gameStateHUD.setGameTime(hours, minutes, seconds);
    }
}
