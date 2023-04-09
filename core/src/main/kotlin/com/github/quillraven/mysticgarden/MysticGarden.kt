package com.github.quillraven.mysticgarden

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.screen.ControlsScreen
import com.github.quillraven.mysticgarden.screen.GameScreen
import com.github.quillraven.mysticgarden.screen.MenuScreen
import com.github.quillraven.mysticgarden.screen.VictoryScreen
import com.github.quillraven.mysticgarden.ui.Bundle
import com.github.quillraven.mysticgarden.ui.get
import com.github.quillraven.mysticgarden.ui.loadSkin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.Scene2DSkin

typealias PhysicWorld = World

class MysticGarden : KtxGame<KtxScreen>() {

    private val batch: Batch by lazy { SpriteBatch() }
    private val assets = Assets()
    private val audioService = AudioService(assets)
    private val uiStage by lazy { Stage(FitViewport(180f, 320f), batch) }
    private val prefs by lazy { Gdx.app.getPreferences("mystic-garden-kotlin") }
    private val eventDispatcher = EventDispatcher()

    override fun create() {
        if (debug) {
            Gdx.app.logLevel = Application.LOG_DEBUG
            prefs.clear()
        } else {
            Gdx.app.logLevel = Application.LOG_ERROR
        }

        // load assets
        assets.load()
        Scene2DSkin.defaultSkin = loadSkin()
        val i18n: I18NBundle = Scene2DSkin.defaultSkin[Bundle.DEFAULT]

        // set audio service volume
        audioService.mscVolume = prefs[prefKeyMusic, 1f]
        audioService.sndVolume = prefs[prefKeySound, 1f]

        // add screens and set start screen
        addScreen(ControlsScreen(this, audioService, uiStage, i18n))
        addScreen(MenuScreen(this, uiStage, audioService, i18n, prefs))
        addScreen(GameScreen(this, batch, assets, prefs, audioService, eventDispatcher, i18n))
        addScreen(VictoryScreen(this, uiStage, i18n, audioService, eventDispatcher))
        setScreen<ControlsScreen>()
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
        getScreen<GameScreen>().saveGame()

        // dispose resources
        super.dispose()
        batch.disposeSafely()
        assets.disposeSafely()
        uiStage.disposeSafely()
        Scene2DSkin.defaultSkin.disposeSafely()
    }

    companion object {
        var isMobile = false
        const val unitScale = 1 / 32f

        const val prefKeyMusic = "musicVolume"
        const val prefKeySound = "soundVolume"
        const val prefKeyPlayerPos = "playerPos"
        const val prefKeyTime = "time"
        const val prefKeyCrystals = "crystals"
        const val prefKeyOrbs = "orbs"
        const val prefKeyItems = "items"
        const val prefKeySpeed = "speed"

        const val b2dPlayer: Short = 2
        const val b2dMapObject: Short = 4
        const val b2dEnvironment: Short = 8
        const val b2dWater: Short = 16

        const val debug = false
        const val debugRender = false
    }
}
