package com.github.quillraven.mysticgarden.ui.model

import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.mysticgarden.component.ItemType
import com.github.quillraven.mysticgarden.event.*
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.system.numCrystals
import com.github.quillraven.mysticgarden.system.numOrbs
import com.github.quillraven.mysticgarden.ui.Bundle
import com.github.quillraven.mysticgarden.ui.I18N
import com.github.quillraven.mysticgarden.ui.get
import ktx.i18n.get
import ktx.scene2d.Scene2DSkin

class GameModel(
    eventDispatcher: EventDispatcher,
    private val keyboardInput: KeyboardInput,
) : PropertyChangeSource() {

    private val i18n: I18NBundle = Scene2DSkin.defaultSkin[Bundle.DEFAULT]
    var infoMsg by propertyNotify("")

    var maxCrystals by propertyNotify(0)
    var collectedCrystals by propertyNotify(0)

    var maxOrbs by propertyNotify(0)
    var collectedOrbs by propertyNotify(0)

    var totalTime by propertyNotify(0)

    var item by propertyNotify(ItemType.NONE)

    init {
        with(eventDispatcher) {
            register<MapChangeEvent> {
                maxCrystals = it.map.numCrystals
                maxOrbs = it.map.numOrbs
            }

            register<CrystalPickupEvent> {
                if (collectedCrystals == 0) {
                    infoMsg = itemPickupInfo("CRYSTAL")
                }
                collectedCrystals = it.crystals
            }

            register<OrbPickupEvent> {
                if (collectedOrbs == 0) {
                    infoMsg = itemPickupInfo("ORB")
                }
                collectedOrbs = it.orbs
            }

            register<GameTimeEvent> { totalTime = it.totalTimeSeconds }

            register<ItemPickupEvent> {
                infoMsg = itemPickupInfo(it.type.name)
                item = it.type
            }
        }
    }

    private fun itemPickupInfo(type: String): String {
        val description = i18n[I18N.ITEM_PICKUP, i18n[I18N.valueOf("ITEM_PICKUP_DESCRIPTION_$type")]]
        val info = i18n[I18N.valueOf("ITEM_PICKUP_INFO_$type")]
        return "$description\n\n$info"
    }

    fun onTouchChange(x: Float, y: Float) {
        keyboardInput.updateMove(x, y)
    }
}