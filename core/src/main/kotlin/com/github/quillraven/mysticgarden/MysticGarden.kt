package com.github.quillraven.mysticgarden

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.mysticgarden.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

typealias PhysicWorld = World

class MysticGarden : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets = Assets()
    private val uiStage by lazy { Stage(FitViewport(450f, 800f), batch) }
    private val prefs by lazy { Gdx.app.getPreferences("mystic-garden-kotlin") }

    override fun create() {
        if (debug) {
            Gdx.app.logLevel = Application.LOG_DEBUG
            prefs.clear()
        } else {
            Gdx.app.logLevel = Application.LOG_ERROR
        }

        assets.load()

        addScreen(GameScreen(batch, assets, uiStage, prefs))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        batch.disposeSafely()
        assets.disposeSafely()
        uiStage.disposeSafely()
    }

    companion object {
        const val unitScale = 1 / 32f
        const val debug = true
    }
}
