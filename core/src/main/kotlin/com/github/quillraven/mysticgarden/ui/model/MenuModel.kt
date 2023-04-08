package com.github.quillraven.mysticgarden.ui.model

import com.badlogic.gdx.Gdx
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.screen.GameScreen

class MenuModel(
    private val game: MysticGarden,
    private val audioService: AudioService
) : PropertyChangeSource() {

    var hasSaveState by propertyNotify(false)
    var volume by propertyNotify(1f)

    fun startNewGame() {
        game.setScreen<GameScreen>()
        game.getScreen<GameScreen>().newGame()
    }

    fun continueGame() {
        game.setScreen<GameScreen>()
        game.getScreen<GameScreen>().loadGame()
    }

    fun quitGame() {
        Gdx.app.exit()
    }

    fun volume(value: Float) {
        audioService.sndVolume = value.coerceIn(0f, 1f)
        audioService.mscVolume = value.coerceIn(0f, 1f)
    }
}