package com.quillraven.game.ecs;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.quillraven.game.core.ecs.component.AnimationComponent;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.core.ecs.component.ParticleEffectComponent;
import com.quillraven.game.core.ecs.system.LightSystem;
import com.quillraven.game.ecs.component.GameObjectComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.ecs.system.*;
import com.quillraven.game.map.GameObject;
import com.quillraven.game.map.LightData;

import static com.quillraven.game.MysticGarden.*;

public class ECSEngine extends com.quillraven.game.core.ecs.EntityEngine {
    private static final String TAG = ECSEngine.class.getSimpleName();

    public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
    public static final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);

    private final World world;
    private final RayHandler rayHandler;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    private final ImmutableArray<Entity> gameObjEntities;

    public ECSEngine(final World world, final RayHandler rayHandler, final OrthographicCamera gameCamera) {
        super();
        this.world = world;
        this.rayHandler = rayHandler;
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        gameObjEntities = getEntitiesFor(Family.all(GameObjectComponent.class).get());


        // iterating systems
        addSystem(new PlayerAnimationSystem());
        addSystem(new PlayerMovementSystem());
        addSystem(new PlayerCameraSystem(gameCamera));
        // player contact system does not need processing because it is triggered by WorldContactManager
        addSystem(new PlayerContactSystem(gameObjEntities));
        getSystem(PlayerContactSystem.class).setProcessing(false);
        addSystem(new LightSystem());
        // ambient light system does not need processing because it is triggered by PlayerContactSystem
        addSystem(new AmbientLightSystem(rayHandler));
        getSystem(AmbientLightSystem.class).setProcessing(false);

        // interval systems
        addSystem(new GameTimeSystem());

        // render systems
        addRenderSystem(new GameRenderSystem(this, world, rayHandler, gameCamera));
    }

    public ImmutableArray<Entity> getGameObjectEntities() {
        return gameObjEntities;
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
        fixtureDef.filter.categoryBits = BIT_PLAYER;
        fixtureDef.filter.maskBits = BIT_GAME_OBJECT | BIT_WORLD | BIT_WATER;
        b2dCmp.body.createFixture(fixtureDef);
        shape.dispose();
        player.add(b2dCmp);
        // light
        b2dCmp.lightDistance = 6;
        b2dCmp.light = new PointLight(rayHandler, 64, new Color(1, 1, 1, 0.7f), b2dCmp.lightDistance, spawnLocation.x, spawnLocation.y);
        b2dCmp.light.attachToBody(b2dCmp.body);
        b2dCmp.lightFluctuationSpeed = 4;
        b2dCmp.lightFluctuationDistance = b2dCmp.light.getDistance() * 0.15f;

        final PlayerComponent playerCmp = createComponent(PlayerComponent.class);
        playerCmp.maxSpeed = 3;
        player.add(playerCmp);

        final AnimationComponent aniCmp = createComponent(AnimationComponent.class);
        player.add(aniCmp);

        addEntity(player);
    }

    public void addGameObject(final GameObject gameObject, final Animation<Sprite> animation) {
        final Entity gameObjEntity = createEntity();
        final Rectangle boundaries = gameObject.getBoundaries();
        final LightData lightData = gameObject.getLightData();
        final float spawnX = boundaries.x + boundaries.width * 0.5f;
        final float spawnY = boundaries.y + boundaries.height * 0.5f;

        final Box2DComponent b2dCmp = createComponent(Box2DComponent.class);
        b2dCmp.width = boundaries.width;
        b2dCmp.height = boundaries.height;
        // body
        bodyDef.gravityScale = 0;
        bodyDef.position.set(spawnX, spawnY);
        b2dCmp.positionBeforeUpdate.set(bodyDef.position);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        b2dCmp.body = world.createBody(bodyDef);
        b2dCmp.body.setUserData(gameObjEntity);
        // fixtures
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(b2dCmp.width * 0.5f, b2dCmp.height * 0.5f);
        fixtureDef.isSensor = false;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = BIT_PLAYER;
        b2dCmp.body.createFixture(fixtureDef);
        shape.dispose();
        gameObjEntity.add(b2dCmp);

        final AnimationComponent aniCmp = createComponent(AnimationComponent.class);
        aniCmp.width = boundaries.width;
        aniCmp.height = boundaries.height;
        aniCmp.animation = animation;
        gameObjEntity.add(aniCmp);

        final GameObjectComponent gameObjCmp = createComponent(GameObjectComponent.class);
        gameObjCmp.type = gameObject.getType();
        gameObjCmp.id = gameObject.getId();
        gameObjEntity.add(gameObjCmp);

        if (lightData != null) {
            b2dCmp.lightDistance = lightData.getDistance();
            if ("point".equals(lightData.getType())) {
                // position is automatically aligned with the b2d body so there is no need to set them in the constructor
                b2dCmp.light = new PointLight(rayHandler, 64, lightData.getColor(), b2dCmp.lightDistance, 0, 0);
                ((PointLight) b2dCmp.light).attachToBody(b2dCmp.body, lightData.getOffsetX(), lightData.getOffsetY());
            } else if ("cone".equals(lightData.getType())) {
                // position and direction are automatically aligned with the b2d body so there is no need to set them in the constructor
                b2dCmp.light = new ConeLight(rayHandler, 64, lightData.getColor(), b2dCmp.lightDistance, 0, 0, 0, lightData.getConeDegree());
                ((ConeLight) b2dCmp.light).attachToBody(b2dCmp.body, lightData.getOffsetX(), lightData.getOffsetY());
                b2dCmp.body.setTransform(b2dCmp.body.getPosition(), lightData.getConeDirection() * MathUtils.degRad);
                b2dCmp.light.setXray(true);
            } else {
                Gdx.app.error(TAG, "Unsupported light type: " + lightData.getType());
            }
            if (lightData.getFluctuation() > 0) {
                b2dCmp.lightFluctuationDistance = b2dCmp.light.getDistance() * lightData.getFluctuation();
                b2dCmp.lightFluctuationSpeed = lightData.getFluctuationSpeed();
            }
        }

        if (gameObject.getParticleType() != ParticleEffectComponent.ParticleEffectType.NOT_DEFINED) {
            final ParticleEffectComponent peCmp = createComponent(ParticleEffectComponent.class);
            peCmp.scaling = gameObject.getParticleScale();
            peCmp.position.set(spawnX + gameObject.getParticleOffsetX(), spawnY + gameObject.getParticleOffsetY());
            peCmp.type = gameObject.getParticleType();
            gameObjEntity.add(peCmp);
        }

        addEntity(gameObjEntity);
    }
}
