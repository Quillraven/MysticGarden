package com.quillraven.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.quillraven.game.core.PreferenceManager;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.core.ecs.component.RemoveComponent;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.ecs.system.AmbientLightSystem;
import com.quillraven.game.ecs.system.GameTimeSystem;
import com.quillraven.game.map.MapManager;
import com.quillraven.game.ui.GameUI;

public class SaveState implements Json.Serializable {
    public static final String SAVE_STATE_PREFERENCE_KEY = "saveState";
    private static final String SAVE_STATE_POS_X_KEY = "playerPosX";
    private static final String SAVE_STATE_POS_Y_KEY = "playerPosY";
    private static final String SAVE_STATE_CRYSTALS_KEY = "crystals";
    private static final String SAVE_STATE_CHROMAORB_KEY = "chromaOrbs";
    private static final String SAVE_STATE_HAS_AXE_KEY = "hasAxe";
    private static final String SAVE_STATE_REMAINING_GAME_OBJ_IDS_KEY = "remainingGameObjects";
    private static final String SAVE_STATE_SECONDS_KEY = "seconds";
    private static final String SAVE_STATE_MINUTES_KEY = "minutes";
    private static final String SAVE_STATE_HOURS_KEY = "hours";

    private final Vector2 playerPos;
    private int crystals;
    private int chromaOrbs;
    private boolean hasAxe;
    private final Array<Integer> gameObjectIDs;
    private int hours;
    private int minutes;
    private int seconds;

    private final ComponentMapper<Box2DComponent> b2dCmpMapper;
    private final ComponentMapper<PlayerComponent> playerCmpMapper;
    private final ComponentMapper<RemoveComponent> removeCmpMapper;
    private final ComponentMapper<GameObjectComponent> gameObjCmpMapper;

    private final Json json;
    private final JsonReader jsonReader;

    public SaveState() {
        playerPos = new Vector2();
        gameObjectIDs = new Array<>();
        playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
        gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
        b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
        removeCmpMapper = ComponentMapper.getFor(RemoveComponent.class);
        json = new Json();
        jsonReader = new JsonReader();
    }

    public void updateState(final Entity player, final ImmutableArray<Entity> gameObjects, final ECSEngine ecsEngine) {
        final Box2DComponent b2dCmp = b2dCmpMapper.get(player);
        final PlayerComponent playerCmp = playerCmpMapper.get(player);

        playerPos.set(b2dCmp.body.getPosition());
        crystals = playerCmp.crystals;
        chromaOrbs = playerCmp.chromaOrbs;
        hasAxe = playerCmp.hasAxe;

        final GameTimeSystem gameTimeSystem = ecsEngine.getSystem(GameTimeSystem.class);
        seconds = gameTimeSystem.getSeconds();
        minutes = gameTimeSystem.getMinutes();
        hours = gameTimeSystem.getHours();

        gameObjectIDs.clear();
        for (final Entity gameObj : gameObjects) {
            gameObjectIDs.add(gameObjCmpMapper.get(gameObj).id);
        }

        PreferenceManager.INSTANCE.setStringValue(SAVE_STATE_PREFERENCE_KEY, json.toJson(this));
    }

    public void loadState(final Entity player, final ImmutableArray<Entity> gameObjects, final ECSEngine ecsEngine, final GameUI gameStateHUD) {
        final PlayerComponent playerCmp = playerCmpMapper.get(player);

        MapManager.INSTANCE.spawnGameObjects(ecsEngine, gameObjects);
        if (!PreferenceManager.INSTANCE.containsKey(SAVE_STATE_PREFERENCE_KEY)) {
            // no save state yet or old state was cleared -> load default settings
            playerPos.set(MapManager.INSTANCE.getCurrentMap().getStartLocation());
            crystals = 0;
            chromaOrbs = 0;
            hasAxe = false;
            hours = 0;
            minutes = 0;
            seconds = 0;
            gameObjectIDs.clear();
            for (final Entity gameObj : gameObjects) {
                gameObjectIDs.add(gameObjCmpMapper.get(gameObj).id);
            }
        } else {
            // load values from preference
            final JsonValue saveStateJsonVal = jsonReader.parse(PreferenceManager.INSTANCE.getStringValue(SAVE_STATE_PREFERENCE_KEY));
            playerPos.x = saveStateJsonVal.getFloat(SAVE_STATE_POS_X_KEY);
            playerPos.y = saveStateJsonVal.getFloat(SAVE_STATE_POS_Y_KEY);
            crystals = saveStateJsonVal.getInt(SAVE_STATE_CRYSTALS_KEY);
            chromaOrbs = saveStateJsonVal.getInt(SAVE_STATE_CHROMAORB_KEY);
            hasAxe = saveStateJsonVal.getBoolean(SAVE_STATE_HAS_AXE_KEY);
            hours = saveStateJsonVal.getInt(SAVE_STATE_HOURS_KEY, 0);
            minutes = saveStateJsonVal.getInt(SAVE_STATE_MINUTES_KEY, 0);
            seconds = saveStateJsonVal.getInt(SAVE_STATE_SECONDS_KEY, 0);

            final JsonValue remainingIDsJsonVal = saveStateJsonVal.get(SAVE_STATE_REMAINING_GAME_OBJ_IDS_KEY);
            if (remainingIDsJsonVal != null) {
                gameObjectIDs.clear();
                final int[] remainingGameObjIDs = remainingIDsJsonVal.asIntArray();
                for (final int id : remainingGameObjIDs) {
                    gameObjectIDs.add(id);
                }
            }
        }

        // set real game model values
        b2dCmpMapper.get(player).body.setTransform(playerPos, 0);
        playerCmp.crystals = crystals;
        playerCmp.chromaOrbs = chromaOrbs;
        playerCmp.hasAxe = hasAxe;
        for (final Entity gameObj : gameObjects) {
            if (removeCmpMapper.get(gameObj) == null && !gameObjectIDs.contains(gameObjCmpMapper.get(gameObj).id, true)) {
                gameObj.add(ecsEngine.createComponent(RemoveComponent.class));
            }
        }

        // update ECS systems
        ecsEngine.getSystem(AmbientLightSystem.class).chromaOrbContact(chromaOrbs);
        ecsEngine.getSystem(GameTimeSystem.class).setTime(hours, minutes, seconds);

        // update HUD
        gameStateHUD.setAxe(hasAxe);
        gameStateHUD.setCrystals(crystals);
        gameStateHUD.setChromaOrb(chromaOrbs);
    }

    @Override
    public void write(final Json json) {
        json.writeValue(SAVE_STATE_POS_X_KEY, playerPos.x);
        json.writeValue(SAVE_STATE_POS_Y_KEY, playerPos.y);
        json.writeValue(SAVE_STATE_CRYSTALS_KEY, crystals);
        json.writeValue(SAVE_STATE_CHROMAORB_KEY, chromaOrbs);
        json.writeValue(SAVE_STATE_HAS_AXE_KEY, hasAxe);
        json.writeValue(SAVE_STATE_REMAINING_GAME_OBJ_IDS_KEY, gameObjectIDs, Array.class, Integer.class);
        json.writeValue(SAVE_STATE_SECONDS_KEY, seconds);
        json.writeValue(SAVE_STATE_MINUTES_KEY, minutes);
        json.writeValue(SAVE_STATE_HOURS_KEY, hours);
    }

    @Override
    public void read(final Json json, final JsonValue jsonData) {
        // nothing to do because SaveState is not instantiated via json
    }
}
