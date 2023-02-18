package com.github.quillraven.mysticgarden

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set

typealias PhysicWorld = World

class MysticGarden : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets = Assets()
    private val audioService = AudioService(assets)
    private val uiStage by lazy { Stage(FitViewport(450f, 800f), batch) }
    private val prefs by lazy { Gdx.app.getPreferences("mystic-garden-kotlin") }

    override fun create() {
        if (debug) {
            Gdx.app.logLevel = Application.LOG_DEBUG
            prefs.clear()
        } else {
            Gdx.app.logLevel = Application.LOG_ERROR
        }

        // load assets
        assets.load()

        // set audio service volume
        audioService.mscVolume = prefs[prefKeyMusic, 1f]
        audioService.sndVolume = prefs[prefKeySound, 1f]

        // add screens and set start screen
        addScreen(GameScreen(batch, assets, uiStage, prefs, audioService))
        setScreen<GameScreen>()
    }

    override fun render() {
        super.render()
        audioService.update()
    }

    override fun dispose() {
        // store volume settings for next session
        prefs.flush {
            this[prefKeyMusic] = audioService.mscVolume
            this[prefKeySound] = audioService.sndVolume
        }

        // dispose resources
        super.dispose()
        batch.disposeSafely()
        assets.disposeSafely()
        uiStage.disposeSafely()
    }

    companion object {
        const val unitScale = 1 / 32f

        const val prefKeyMusic = "musicVolume"
        const val prefKeySound = "soundVolume"

        const val b2dPlayer: Short = 2
        const val b2dMapObject: Short = 4
        const val b2dEnvironment: Short = 8
        const val b2dWater: Short = 16

        const val debug = true
    }
}
