package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.GameTimeEvent
import com.github.quillraven.mysticgarden.event.MapChangeEvent

class GameTimeSystem(private val eventDispatcher: EventDispatcher = inject()) : IntervalSystem(interval = Fixed(1f)) {
    private var totalTime = 0

    init {
        eventDispatcher.register<MapChangeEvent> { totalTime = 0 }
    }

    override fun onTick() {
        totalTime += 1
        eventDispatcher.dispatch(GameTimeEvent(totalTime))
    }

    fun resetTime() {
        totalTime = 0
    }
}