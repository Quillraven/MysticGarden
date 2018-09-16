package com.quillraven.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.Map;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.core.ecs.component.AnimationComponent;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.core.ecs.system.AnimationSystem;
import com.quillraven.game.ecs.system.GameRenderSystem;
import com.quillraven.game.ecs.system.PlayerCameraSystem;
import com.quillraven.game.ecs.system.PlayerMovementSystem;

public class ECSEngine extends com.quillraven.game.core.ecs.EntityEngine {
    private static final String TAG = ECSEngine.class.getSimpleName();

    private final World world;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public ECSEngine(final Game game, final World world, final OrthographicCamera gameCamera, final Map map) {
        super();
        this.world = world;
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();

        final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
        final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
        final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
        // iterating systems
        addSystem(new AnimationSystem(aniCmpMapper));
        addSystem(new PlayerMovementSystem(playerCmpMapper, b2dCmpMapper));
        addSystem(new PlayerCameraSystem(gameCamera, b2dCmpMapper));
        // render systems
        addRenderSystem(new GameRenderSystem(this, game, world, gameCamera, map, b2dCmpMapper, aniCmpMapper));
    }

    public void addPlayer(final float x, final float y) {
        final Entity player = createEntity();

        final Box2DComponent b2dCmp = createComponent(Box2DComponent.class);
        b2dCmp.width = 1;
        b2dCmp.height = 1;
        // body
        bodyDef.gravityScale = 1;
        bodyDef.position.set(x, y);
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

        addEntity(player);
    }

    public void addGameObject(final float x, final float y, final float width, final float height, final TiledMapTile tile) {
        final Entity gameObj = createEntity();

        final Box2DComponent b2dCmp = createComponent(Box2DComponent.class);
        b2dCmp.width = width;
        b2dCmp.height = height;
        // body
        bodyDef.gravityScale = 0;
        bodyDef.position.set(x + width * 0.5f, y + height * 0.5f);
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
        aniCmp.width = width;
        aniCmp.height = height;
        if (tile instanceof AnimatedTiledMapTile) {
            final AnimatedTiledMapTile aniTile = (AnimatedTiledMapTile) tile;
            final Array<Sprite> keyFrames = new Array<>();
            for (final StaticTiledMapTile staticTile : aniTile.getFrameTiles()) {
                keyFrames.add(new Sprite(staticTile.getTextureRegion()));
            }
            aniCmp.animation = new Animation<>(aniTile.getAnimationIntervals()[0] * 0.001f, keyFrames, Animation.PlayMode.LOOP);
        } else if (tile instanceof StaticTiledMapTile) {
            aniCmp.animation = new Animation<>(0, new Sprite(tile.getTextureRegion()));
        } else {
            Gdx.app.error(TAG, "Unsupported TiledMapTile type " + tile);
        }
        gameObj.add(aniCmp);

        addEntity(gameObj);
    }
}
