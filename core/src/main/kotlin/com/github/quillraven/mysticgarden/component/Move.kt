package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.math.vec2

data class Move(var maxSpeed: Float, val speed: Vector2 = vec2()) : Component<Move> {
    override fun type(): ComponentType<Move> = Move

    companion object : ComponentType<Move>() {
        const val defaultSpeed = 3f
    }
}