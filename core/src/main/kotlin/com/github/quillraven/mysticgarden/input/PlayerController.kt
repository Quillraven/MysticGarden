package com.github.quillraven.mysticgarden.input

import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.component.Physic
import com.github.quillraven.mysticgarden.component.Player
import ktx.math.component1
import ktx.math.component2

class PlayerController(world: World) {
    private val players = world.family { all(Player) }

    fun move(x: Float, y: Float) {
        val angle = MathUtils.atan2(y, x)

        players.forEach {
            val (body, _, impulse) = it[Physic]
            val (velX, velY) = body.linearVelocity

            if (x == 0f && y == 0f) {
                // stop player
                impulse.set(body.mass * (-velX), body.mass * (-velY))
            } else {
                impulse.set(
                    body.mass * (playerSpeed * MathUtils.cos(angle) - velX),
                    body.mass * (playerSpeed * MathUtils.sin(angle) - velY),
                )
            }
        }
    }

    companion object {
        private const val playerSpeed = 3f
    }
}