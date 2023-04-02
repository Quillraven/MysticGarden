package com.github.quillraven.mysticgarden.screen

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.world
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.MusicAsset
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.TiledMapAsset
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.component.B2DLight
import com.github.quillraven.mysticgarden.component.Light
import com.github.quillraven.mysticgarden.component.Light.Companion.LightCone
import com.github.quillraven.mysticgarden.component.Light.Companion.LightPoint
import com.github.quillraven.mysticgarden.component.Particle
import com.github.quillraven.mysticgarden.component.Physic
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.input.PlayerController
import com.github.quillraven.mysticgarden.system.*
import com.github.quillraven.mysticgarden.ui.model.GameModel
import com.github.quillraven.mysticgarden.ui.view.gameView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.scene2d.actors
import kotlin.experimental.or

class GameScreen(
    private val game: MysticGarden,
    private val batch: Batch,
    private val assets: Assets,
    private val uiStage: Stage,
    private val prefs: Preferences,
    private val audioService: AudioService,
    private val eventDispatcher: EventDispatcher,
    private val i18n: I18NBundle,
) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(6.75f, 12f, gameCamera)
    private val physicWorld = createWorld()
    private val rayHandler = createRayHandler()
    private val world = createEntityWorld()
    private val keyboardInput = KeyboardInput(PlayerController(world))

    private fun createEntityWorld(): World = world {
        injectables {
            add(game)
            add(batch)
            add(gameCamera)
            add(gameViewport)
            add(uiStage)
            add(eventDispatcher)
            add(assets)
            add(physicWorld)
            add(prefs)
            add(rayHandler)
            add(audioService)
        }

        components {
            onRemove(Physic, Physic.onRemove)
            onRemove(LightPoint, Light.onRemove)
            onRemove(LightCone, Light.onRemove)
            onRemove(Particle, Particle.onRemove)
        }

        systems {
            add(GameTimeSystem())
            add(MapSystem())
            add(PhysicSystem())
            add(CollisionSystem())
            add(AnimationSystem())
            add(DisableSystem())
            add(ZoneSystem())
            add(CameraSystem())
            add(RenderSystem())
            add(ParticleSystem())
            add(LightSystem())
            add(RemoveSystem())
            if (MysticGarden.debugRender) {
                add(DebugRenderSystem())
            }
        }
    }

    private fun createRayHandler() = RayHandler(physicWorld).apply {
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

    override fun show() {
        uiStage.clear()
        uiStage.actors {
            gameView(i18n, GameModel(eventDispatcher, keyboardInput, i18n), true)
        }

        Gdx.input.inputProcessor = InputMultiplexer(keyboardInput, uiStage)

        eventDispatcher.dispatch(MapChangeEvent(assets[TiledMapAsset.MAP]))
        audioService.play(MusicAsset.GAME)
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

        // render UI
        uiStage.viewport.apply()
        uiStage.act(dt)
        uiStage.draw()
        // reset alpha value for game rendering
        batch.color.a = 1f
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
