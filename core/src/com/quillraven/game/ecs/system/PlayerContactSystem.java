package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.WorldContactManager;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.ecs.component.RemoveComponent;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;

public class PlayerContactSystem extends EntitySystem implements WorldContactManager.WorldContactListener {
    private final ImmutableArray<Entity> gameObjEntities;
    private final Array<PlayerContactListener> listeners;
    private final AudioManager audioManager;

    public PlayerContactSystem(final ImmutableArray<Entity> gameObjEntities) {
        this.gameObjEntities = gameObjEntities;
        listeners = new Array<>();
        Utils.getWorldContactManager().addWorldContactListener(this);
        this.audioManager = Utils.getAudioManager();
    }

    public void addPlayerContactListener(final PlayerContactListener listener) {
        listeners.add(listener);
    }

    @Override
    public void beginContact(final Entity player, final Entity gameObject) {
        final GameObjectComponent gameObjCmp = ECSEngine.gameObjCmpMapper.get(gameObject);
        final PlayerComponent playerCmp = ECSEngine.playerCmpMapper.get(player);
        switch (gameObjCmp.type) {
            case CRYSTAL:
                crystalContact(playerCmp, gameObject);
                break;
            case CHROMA_ORB:
                chromaOrbContact(playerCmp, gameObject);
                break;
            case CLUB:
            case WAND:
            case AXE:
                itemContact(playerCmp, gameObject, gameObjCmp);
                break;
            case TREE:
                if (playerCmp.hasAxe) {
                    audioManager.playAudio(AudioManager.AudioType.CHOP);
                    gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                }
                break;
            case WALL:
                if (playerCmp.hasClub) {
                    audioManager.playAudio(AudioManager.AudioType.SMASH);
                    gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                }
                break;
            case FIRESTONE:
                if (playerCmp.hasWand) {
                    audioManager.playAudio(AudioManager.AudioType.SWING);
                    gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                }
                break;
            case PORTAL:
                final boolean hasAllCrystals = playerCmp.crystals == Utils.getMapManager().getCurrentMap().getNumCrystals();
                for (final PlayerContactListener listener : listeners) {
                    listener.portalContact(hasAllCrystals);
                }
                break;
            default:
                // nothing to do
                break;
        }
    }

    private void itemContact(final PlayerComponent playerCmp, final Entity gameObject, final GameObjectComponent gameObjCmp) {
        audioManager.playAudio(AudioManager.AudioType.JINGLE);
        playerCmp.sleepTime = 3f;
        gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
        if (gameObjCmp.type == GameObjectComponent.GameObjectType.AXE) {
            playerCmp.hasAxe = true;
        } else if (gameObjCmp.type == GameObjectComponent.GameObjectType.WAND) {
            playerCmp.hasWand = true;
        } else if (gameObjCmp.type == GameObjectComponent.GameObjectType.CLUB) {
            playerCmp.hasClub = true;
        }
        for (final PlayerContactListener listener : listeners) {
            listener.itemContact(gameObjCmp.type);
        }
    }

    private void chromaOrbContact(final PlayerComponent playerCmp, final Entity gameObject) {
        audioManager.playAudio(AudioManager.AudioType.JINGLE);
        playerCmp.sleepTime = 1.5f;
        gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
        ++playerCmp.chromaOrbs;
        for (final PlayerContactListener listener : listeners) {
            listener.chromaOrbContact(playerCmp.chromaOrbs);
        }
    }

    private void crystalContact(final PlayerComponent playerCmp, final Entity gameObject) {
        audioManager.playAudio(AudioManager.AudioType.CRYSTAL_PICKUP);
        gameObject.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
        ++playerCmp.crystals;
        for (final PlayerContactListener listener : listeners) {
            listener.crystalContact(playerCmp.crystals);
        }

        if (playerCmp.crystals == 1) {
            // first crystal picked up --> destroy tutorial trees that block the path
            for (final Entity gameObj : gameObjEntities) {
                if (ECSEngine.gameObjCmpMapper.get(gameObj).type == GameObjectComponent.GameObjectType.TUTORIAL_TREE) {
                    gameObj.add(((ECSEngine) this.getEngine()).createComponent(RemoveComponent.class));
                }
            }
        }
    }

    public interface PlayerContactListener {
        void crystalContact(final int crystalsFound);

        void itemContact(final GameObjectComponent.GameObjectType type);

        void chromaOrbContact(final int chromaOrbsFound);

        void portalContact(final boolean hasAllCrystals);
    }
}
