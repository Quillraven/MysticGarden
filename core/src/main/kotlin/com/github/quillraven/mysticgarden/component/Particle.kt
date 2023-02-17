package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentHook
import com.github.quillraven.fleks.ComponentType
import ktx.math.vec2

data class Particle(
    val effect: PooledEffect,
    val offset: Vector2 = vec2(),
    val speed: Float = 1f
) : Component<Particle> {

    init {
        effect.start()
    }

    override fun type() = Particle

    companion object : ComponentType<Particle>() {
        val onRemove: ComponentHook<Particle> = { _, component ->
            component.effect.free()
        }
    }
}