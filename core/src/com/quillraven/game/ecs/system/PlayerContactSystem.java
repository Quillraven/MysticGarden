package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.WorldContactManager;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.ecs.component.RemoveComponent;

public class PlayerContactSystem extends EntitySystem implements WorldContactManager.WorldContactListener {
    private final ComponentMapper<PlayerComponent> playerCmpMapper;
    private final ComponentMapper<GameObjectComponent> gameObjCmpMapper;
    private final Array<PlayerContactListener> listeners;

    public PlayerContactSystem(final ComponentMapper<PlayerComponent> playerCmpMapper, final ComponentMapper<GameObjectComponent> gameObjCmpMapper) {
        this.playerCmpMapper = playerCmpMapper;
        this.gameObjCmpMapper = gameObjCmpMapper;
        listeners = new Array<>();
        WorldContactManager.INSTANCE.addWorldContactListener(this);
    }

    public void addPlayerContactListener(final PlayerContactListener listener) {
        listeners.add(listener);
    }

    @Override
    public void beginContact(final Entity player, final Entity gameObject) {
        final GameObjectComponent gameObjCmp = gameObjCmpMapper.get(gameObject);
        final PlayerComponent playerCmp = playerCmpMapper.get(player);
        switch (gameObjCmp.type) {
            case CRYSTAL:
                AudioManager.INSTANCE.playAudio(AudioManager.AudioType.CRYSTAL_PICKUP);
                gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                ++playerCmp.crystals;
                for (final PlayerContactListener listener : listeners) {
                    listener.crystalContact(playerCmp.crystals);
                }
                break;
            case AXE:
                AudioManager.INSTANCE.playAudio(AudioManager.AudioType.JINGLE);
                playerCmp.sleepTime = 3f;
                gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                playerCmp.hasAxe = true;
                for (final PlayerContactListener listener : listeners) {
                    listener.itemContact(gameObjCmp.type);
                }
                break;
            case TREE:
                if (playerCmp.hasAxe) {
                    AudioManager.INSTANCE.playAudio(AudioManager.AudioType.CHOP);
                    gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                }
                break;
            default:
                // nothing to do
                break;
        }
    }

    public interface PlayerContactListener {
        void crystalContact(final int crystalsFound);

        void itemContact(final GameObjectComponent.GameObjectType type);
    }
}
