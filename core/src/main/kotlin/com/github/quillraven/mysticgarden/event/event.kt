package com.github.quillraven.mysticgarden.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.mysticgarden.component.ItemType
import com.github.quillraven.mysticgarden.system.Zone
import kotlin.reflect.KClass

class EventDispatcher {
    @PublishedApi
    internal val actions = mutableMapOf<KClass<out Event>, MutableList<(Event) -> Unit>>()

    inline fun <reified T : Event> register(noinline hook: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        actions.getOrPut(T::class) { mutableListOf() }.add(hook as (Event) -> Unit)
    }

    inline fun <reified T : Event> dispatch(event: T) {
        actions[T::class]?.forEach { it(event) }
    }
}

sealed interface Event

data class MapChangeEvent(val map: TiledMap) : Event

data class ZoneChangeEvent(val position: Vector2, val map: TiledMap, val newZone: Zone, val oldZone: Zone) : Event

data class CrystalPickupEvent(val crystals: Int) : Event

data class OrbPickupEvent(val orbs: Int) : Event

data class GameTimeEvent(val totalTimeSeconds: Int) : Event

data class ItemPickupEvent(val type: ItemType) : Event

object PortalCollision : Event