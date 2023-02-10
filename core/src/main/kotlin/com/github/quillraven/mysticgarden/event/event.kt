package com.github.quillraven.mysticgarden.event

import com.badlogic.gdx.maps.tiled.TiledMap
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