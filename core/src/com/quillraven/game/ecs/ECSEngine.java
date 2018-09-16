package com.quillraven.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.ecs.system.GameRenderSystem;
import com.quillraven.game.ecs.system.PlayerMovementSystem;

public class ECSEngine extends com.quillraven.game.core.ecs.EntityEngine {
    private final World world;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public ECSEngine(final Game game) {
        super();
        this.world = game.getWorld();
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();

        final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
        final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
        // iterating systems
        addSystem(new PlayerMovementSystem(playerCmpMapper, b2dCmpMapper));
        // render systems
        addRenderSystem(new GameRenderSystem(this, game));
    }

    public void addPlayer(final float x, final float y) {
        final Entity player = createEntity();

        final Box2DComponent b2dCmp = createComponent(Box2DComponent.class);
        b2dCmp.width = 1;
        b2dCmp.height = 1;
        b2dCmp.positionBeforeUpdate.set(x, y);
        // body
        bodyDef.gravityScale = 1;
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dCmp.body = world.createBody(bodyDef);
        b2dCmp.body.setUserData(player);
        // fixtures
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(b2dCmp.width * 0.5f, b2dCmp.height * 0.5f);
        fixtureDef.isSensor = false;
        fixtureDef.shape = shape;
        b2dCmp.body.createFixture(fixtureDef);
        shape.dispose();
        player.add(b2dCmp);

        final PlayerComponent playerCmp = createComponent(PlayerComponent.class);
        playerCmp.maxSpeed = 3;
        player.add(playerCmp);

        addEntity(player);
    }
}
