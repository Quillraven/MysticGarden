package com.github.quillraven.mysticgarden.ui.model

import com.github.quillraven.mysticgarden.event.*
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.system.numCrystals
import com.github.quillraven.mysticgarden.system.numOrbs

class GameModel(
    eventDispatcher: EventDispatcher,
    private val keyboardInput: KeyboardInput,
) : PropertyChangeSource() {

    var maxCrystals by propertyNotify(0)
    var collectedCrystals by propertyNotify(0)

    var maxOrbs by propertyNotify(0)
    var collectedOrbs by propertyNotify(0)

    var totalTime by propertyNotify(0)

    init {
        with(eventDispatcher) {
            register<MapChangeEvent> {
                maxCrystals = it.map.numCrystals
                maxOrbs = it.map.numOrbs
            }

            register<CrystalPickupEvent> { collectedCrystals = it.crystals }
            register<OrbPickupEvent> { collectedOrbs = it.orbs }

            register<GameTimeEvent> { totalTime = it.totalTimeSeconds }
        }
    }

    fun onTouchChange(x: Float, y: Float) {
        keyboardInput.updateMove(x, y)
    }
}