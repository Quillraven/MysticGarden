package com.github.quillraven.mysticgarden.input

import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.component.Move
import com.github.quillraven.mysticgarden.component.Player

class PlayerController(world: World) {
    private val players = world.family { all(Player) }

    fun move(x: Float, y: Float) {
        val angle = MathUtils.atan2(y, x)
        val cos = MathUtils.cos(angle)
        val sin = MathUtils.sin(angle)

        players.forEach {
            val (maxSpeed, speed) = it[Move]

            if (x == 0f && y == 0f) {
                speed.setZero()
            } else {
                speed.set(maxSpeed * cos, maxSpeed * sin)
            }
        }
    }
}