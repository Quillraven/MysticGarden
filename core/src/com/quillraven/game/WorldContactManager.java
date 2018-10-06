package com.quillraven.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;

public class WorldContactManager implements ContactListener {
    private final ComponentMapper<PlayerComponent> playerCmpMapper;
    private final ComponentMapper<GameObjectComponent> gameObjectCmpMapper;
    private final Array<WorldContactListener> listeners;
    private Entity player;
    private Entity gameObj;

    public WorldContactManager() {
        playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
        gameObjectCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
        listeners = new Array<>();
        player = null;
        gameObj = null;
    }

    public void addWorldContactListener(final WorldContactListener listener) {
        listeners.add(listener);
    }

    private boolean prepareContactData(final Contact contact) {
        player = null;
        gameObj = null;

        final Object userDataA = contact.getFixtureA().getBody().getUserData();
        final Object userDataB = contact.getFixtureB().getBody().getUserData();
        if (!(userDataA instanceof Entity) || !(userDataB instanceof Entity)) {
            // no entity collision -> ignore
            return false;
        }

        if (playerCmpMapper.get((Entity) userDataA) != null) {
            player = (Entity) userDataA;
        } else if (playerCmpMapper.get((Entity) userDataB) != null) {
            player = (Entity) userDataB;
        } else {
            return false;
        }

        if (gameObjectCmpMapper.get((Entity) userDataA) != null) {
            gameObj = (Entity) userDataA;
        } else if (gameObjectCmpMapper.get((Entity) userDataB) != null) {
            gameObj = (Entity) userDataB;
        } else {
            return false;
        }

        return true;
    }

    @Override
    public void beginContact(final Contact contact) {
        if (prepareContactData(contact)) {
            for (final WorldContactListener listener : listeners) {
                listener.beginContact(player, gameObj);
            }
        }
    }

    @Override
    public void endContact(final Contact contact) {
        // no special logic needed
    }

    @Override
    public void preSolve(final Contact contact, final Manifold oldManifold) {
        // no special logic needed
    }

    @Override
    public void postSolve(final Contact contact, final ContactImpulse impulse) {
        // no special logic needed
    }

    public interface WorldContactListener {
        void beginContact(final Entity player, final Entity gameObject);
    }
}
