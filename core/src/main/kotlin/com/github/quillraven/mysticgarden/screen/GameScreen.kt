package com.github.quillraven.mysticgarden.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.world
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.RegionName
import com.github.quillraven.mysticgarden.component.Animation
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.Render
import com.github.quillraven.mysticgarden.system.AnimationSystem
import com.github.quillraven.mysticgarden.system.RenderSystem
import ktx.app.KtxScreen

class GameScreen(private val batch: Batch, private val assets: Assets, private val uiStage: Stage) : KtxScreen {

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(9f, 16f, gameCamera)

    private val world = world {
        injectables {
            add(batch)
            add(gameCamera)
            add(gameViewport)
            add(uiStage)
        }

        systems {
            add(AnimationSystem())
            add(RenderSystem())
        }
    }

    override fun show() {
        world.entity {
            it += Boundary(1f, 1f)
            it += Render(Sprite(assets[RegionName.FIREBALL]))
        }

        world.entity {
            it += Boundary(2f, 2f, 2f, 2f)
            it += Animation.of(assets, RegionName.HERO_UP)
            it += Render(Sprite())
        }

        world.entity {
            it += Boundary(2f, 5f, 2f, 2f)
            it += Animation.of(assets, RegionName.HERO_DOWN).apply {
                speed = 0.5f
                mode = PlayMode.LOOP_PINGPONG
            }
            it += Render(Sprite())
        }
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
    }
}
