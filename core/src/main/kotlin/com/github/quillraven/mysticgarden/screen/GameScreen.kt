package com.github.quillraven.mysticgarden.screen

import box2dLight.RayHandler
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.world
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.TiledMapAsset
import com.github.quillraven.mysticgarden.component.B2DLight
import com.github.quillraven.mysticgarden.component.Light
import com.github.quillraven.mysticgarden.component.Physic
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.input.PlayerController
import com.github.quillraven.mysticgarden.system.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import kotlin.experimental.or

class GameScreen(
    private val batch: Batch,
    private val assets: Assets,
    private val uiStage: Stage,
    private val prefs: Preferences,
) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(6.75f, 12f, gameCamera)
    private val eventDispatcher = EventDispatcher()
    private val physicWorld = createWorld()
    private val rayHandler = RayHandler(physicWorld).apply {
        // use diffuse light to make the light not super bright
        RayHandler.useDiffuseLight(true)
        // don't throw shadows for water bodies
        // -> only do that between player, objects and environment
        B2DLight.setGlobalContactFilter(
            MysticGarden.b2dPlayer,
            1,
            MysticGarden.b2dMapObject or MysticGarden.b2dEnvironment
        )

        setAmbientLight(Light.ambientColor)
    }

    private val world = world {
        injectables {
            add(batch)
            add(gameCamera)
            add(gameViewport)
            add(uiStage)
            add(eventDispatcher)
            add(assets)
            add(physicWorld)
            add(prefs)
            add(rayHandler)
        }

        components {
            onRemove(Physic, Physic.onRemove)
            onRemove(Light, Light.onRemove)
        }

        systems {
            add(MapSystem())
            add(PhysicSystem())
            add(CollisionSystem())
            add(AnimationSystem())
            add(DisableSystem())
            add(ZoneSystem())
            add(CameraSystem())
            add(RenderSystem())
            add(LightSystem())
            add(RemoveSystem())
            if (MysticGarden.debug) {
                add(DebugRenderSystem())
            }
        }
    }

    override fun show() {
        eventDispatcher.dispatch(MapChangeEvent(assets[TiledMapAsset.MAP]))
        KeyboardInput(PlayerController(world))
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)

        val (screenX, screenY, screenW, screenH) = gameViewport
        rayHandler.useCustomViewport(screenX, screenY, screenW, screenH)
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        world.update(dt)
    }

    override fun dispose() {
        world.dispose()
        physicWorld.disposeSafely()
        rayHandler.disposeSafely()
    }
}

private operator fun Viewport.component1(): Int = this.screenX

private operator fun Viewport.component2(): Int = this.screenY

private operator fun Viewport.component3(): Int = this.screenWidth

private operator fun Viewport.component4(): Int = this.screenHeight
