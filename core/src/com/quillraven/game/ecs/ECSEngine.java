package com.quillraven.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.core.ecs.component.AnimationComponent;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.core.ecs.system.AnimationSystem;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.ecs.system.GameRenderSystem;
import com.quillraven.game.ecs.system.PlayerAnimationSystem;
import com.quillraven.game.ecs.system.PlayerCameraSystem;
import com.quillraven.game.ecs.system.PlayerMovementSystem;

public class ECSEngine extends com.quillraven.game.core.ecs.EntityEngine {
    private final World world;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public ECSEngine(final World world, final OrthographicCamera gameCamera) {
        super();
        this.world = world;
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();

        final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
        final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
        final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
        // iterating systems
        addSystem(new AnimationSystem(aniCmpMapper));
        addSystem(new PlayerAnimationSystem(b2dCmpMapper, playerCmpMapper, aniCmpMapper));
        addSystem(new PlayerMovementSystem(playerCmpMapper, b2dCmpMapper));
        addSystem(new PlayerCameraSystem(gameCamera, b2dCmpMapper));
        // render systems
        addRenderSystem(new GameRenderSystem(this, world, gameCamera, b2dCmpMapper, aniCmpMapper));
    }

    public void addPlayer(final Vector2 spawnLocation) {
        final Entity player = createEntity();

        final Box2DComponent b2dCmp = createComponent(Box2DComponent.class);
        b2dCmp.width = 0.5f;
        b2dCmp.height = 0.5f;
        // body
        bodyDef.gravityScale = 1;
        bodyDef.position.set(spawnLocation);
        b2dCmp.positionBeforeUpdate.set(bodyDef.position);
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

        final AnimationComponent aniCmp = createComponent(AnimationComponent.class);
        player.add(aniCmp);

        addEntity(player);
    }

    public void addGameObject(final Rectangle boundaries, final Animation<Sprite> animation) {
        final Entity gameObj = createEntity();

        final Box2DComponent b2dCmp = createComponent(Box2DComponent.class);
        b2dCmp.width = boundaries.width;
        b2dCmp.height = boundaries.height;
        // body
        bodyDef.gravityScale = 0;
        bodyDef.position.set(boundaries.x + boundaries.width * 0.5f, boundaries.y + boundaries.height * 0.5f);
        b2dCmp.positionBeforeUpdate.set(bodyDef.position);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        b2dCmp.body = world.createBody(bodyDef);
        b2dCmp.body.setUserData(gameObj);
        // fixtures
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(b2dCmp.width * 0.5f, b2dCmp.height * 0.5f);
        fixtureDef.isSensor = false;
        fixtureDef.shape = shape;
        b2dCmp.body.createFixture(fixtureDef);
        shape.dispose();
        gameObj.add(b2dCmp);

        final AnimationComponent aniCmp = createComponent(AnimationComponent.class);
        aniCmp.width = boundaries.width;
        aniCmp.height = boundaries.height;
        aniCmp.animation = animation;
        gameObj.add(aniCmp);

        addEntity(gameObj);
    }
}
