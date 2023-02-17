package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.Particle
import ktx.graphics.use

class ParticleSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject(),
    private val gameCamera: OrthographicCamera = inject(),
) : IteratingSystem(family { all(Particle, Boundary) }) {

    override fun onTick() {
        gameViewport.apply()
        batch.use(gameCamera) {
            val blndSrc = batch.blendSrcFunc
            val blndDst = batch.blendDstFunc

            super.onTick()

            // We need to manually restore blend functions because
            // we set 'setEmittersCleanUpBlendFunction' to false
            // for every effect to optimize draw calls
            batch.setBlendFunction(blndSrc, blndDst)
        }
    }

    override fun onTickEntity(entity: Entity) {
        val (x, y, w) = entity[Boundary]
        val (effect, offset, speed) = entity[Particle]

        with(effect) {
            setPosition(x + 0.5f * w + offset.x, y + offset.y)
            update(deltaTime * speed)
            draw(batch)
        }
    }
}