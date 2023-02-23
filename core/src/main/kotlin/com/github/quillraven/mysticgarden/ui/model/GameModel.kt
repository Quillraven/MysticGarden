package com.github.quillraven.mysticgarden.ui.model

import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.event.CrystalPickupEvent
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.event.OrbPickupEvent
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.system.numCrystals
import com.github.quillraven.mysticgarden.system.numOrbs

class GameModel(
    private val world: World,
    eventDispatcher: EventDispatcher,
    private val keyboardInput: KeyboardInput,
) : PropertyChangeSource() {

    var maxCrystals by propertyNotify(0)
    var collectedCrystals by propertyNotify(0)

    var maxOrbs by propertyNotify(0)
    var collectedOrbs by propertyNotify(0)

    init {
        with(eventDispatcher) {
            register<MapChangeEvent> {
                maxCrystals = it.map.numCrystals
                maxOrbs = it.map.numOrbs
            }

            register<CrystalPickupEvent> { collectedCrystals = it.crystals }
            register<OrbPickupEvent> { collectedOrbs = it.orbs }
        }
    }

    fun onTouchChange(x: Float, y: Float) {
        keyboardInput.updateMove(x, y)
    }
}