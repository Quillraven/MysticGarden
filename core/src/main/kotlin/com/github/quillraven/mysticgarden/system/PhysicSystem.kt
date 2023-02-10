package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.PhysicWorld
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.Physic
import ktx.math.component1
import ktx.math.component2

class PhysicSystem(
    private val physicWorld: PhysicWorld = inject(),
) : IteratingSystem(
    family = family { all(Physic, Boundary) },
    interval = Fixed(stepRate),
) {

    override fun onUpdate() {
        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(stepRate, 6, 2)
    }

    // store position before world.step is called to interpolate the position
    // for a smooth rendering
    override fun onTickEntity(entity: Entity) {
        val (body, posBeforeStep, impulse) = entity[Physic]
        posBeforeStep.set(body.position)

        if (!impulse.isZero) {
            body.applyLinearImpulse(impulse, body.worldCenter, true)
            impulse.setZero()
        }
    }

    // interpolate render position between the position before and after the world.step
    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val (body, posBeforeStep) = entity[Physic]
        val (bodyX, bodyY) = body.position
        val (prevX, prevY) = posBeforeStep

        entity[Boundary].pos(
            MathUtils.lerp(prevX, bodyX, alpha),
            MathUtils.lerp(prevY, bodyY, alpha),
        )
    }

    companion object {
        private const val stepRate = 1 / 60f
    }
}