package com.github.quillraven.mysticgarden.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.world
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.TiledMapAsset
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.input.PlayerController
import com.github.quillraven.mysticgarden.system.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld

class GameScreen(private val batch: Batch, private val assets: Assets, private val uiStage: Stage) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(9f, 16f, gameCamera)
    private val eventDispatcher = EventDispatcher()
    private val physicWorld = createWorld().apply { autoClearForces = false }

    private val world = world {
        injectables {
            add(batch)
            add(gameCamera)
            add(gameViewport)
            add(uiStage)
            add(eventDispatcher)
            add(assets)
            add(physicWorld)
        }

        systems {
            add(MapSystem())
            add(PhysicSystem())
            add(AnimationSystem())
            add(CameraLockSystem())
            add(RenderSystem())
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
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        world.update(dt)
    }

    override fun dispose() {
        world.dispose()
        physicWorld.disposeSafely()
    }
}
