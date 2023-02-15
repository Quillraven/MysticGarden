package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.PhysicWorld
import com.github.quillraven.mysticgarden.component.*
import ktx.math.component1
import ktx.math.component2

class PhysicSystem(
    private val physicWorld: PhysicWorld = inject(),
) : IteratingSystem(
    family = family { all(Physic, Boundary) },
    interval = Fixed(stepRate),
), ContactListener {

    private val physicMoveEntities = world.family { all(Physic, Move) }

    init {
        // to get a consistent physic simulation with a fixed timestep
        // approach, we need to disable autoClearForces
        physicWorld.autoClearForces = false
        physicWorld.setContactListener(this)
    }

    override fun onUpdate() {
        // Apply linear impulse once before doing the world steps
        // to get a consistent behavior with our fixed timestep approach.
        // Forces are cleared manually afterwards (see end of this method)
        applyMoveImpulse()

        super.onUpdate()

        physicWorld.clearForces()
    }

    private fun applyMoveImpulse() {
        physicMoveEntities.forEach {
            val (body) = it[Physic]
            val (worldX, worldY) = body.worldCenter
            val (velX, velY) = body.linearVelocity

            if (it has Disable) {
                // disabled entities will be stopped
                body.applyLinearImpulse(-velX, -velY, worldX, worldY, true)
                return@forEach
            }

            val (_, speed) = it[Move]
            body.applyLinearImpulse(
                body.mass * speed.x - velX,
                body.mass * speed.y - velY,
                worldX, worldY,
                true
            )
        }
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(stepRate, 6, 2)
    }

    // store position before world.step is called to interpolate the position
    // for a smooth rendering
    override fun onTickEntity(entity: Entity) {
        val (body, posBeforeStep) = entity[Physic]
        posBeforeStep.set(body.position)
    }

    // interpolate render position between the position before and after the world.step
    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val (body, posBeforeStep) = entity[Physic]
        val (bodyX, bodyY) = body.position
        val (prevX, prevY) = posBeforeStep
        val boundary = entity[Boundary]
        val (_, _, w, h) = boundary

        boundary.pos(
            MathUtils.lerp(prevX, bodyX, alpha) - w * 0.5f,
            MathUtils.lerp(prevY, bodyY, alpha) - h * 0.5f,
        )
    }

    override fun beginContact(contact: Contact) {
        val dataA = contact.fixtureA.body.userData
        val dataB = contact.fixtureB.body.userData

        if (dataA !is Entity || dataB !is Entity) {
            return
        }

        // Store collision events to handle them afterwards because there are some
        // limitations within ContactListener functions like e.g. you are not allowed
        // to remove bodies (=our entities).
        if (dataA has Player) {
            dataA.configure {
                val (collEntities) = it.getOrAdd(Collision) { Collision() }
                collEntities.add(dataB)
            }
        } else if (dataB has Player) {
            dataB.configure {
                val (collEntities) = it.getOrAdd(Collision) { Collision() }
                collEntities.add(dataA)
            }
        }
    }

    override fun endContact(contact: Contact) = Unit

    override fun preSolve(contact: Contact, oldManifold: Manifold) = Unit

    override fun postSolve(contact: Contact, impulse: ContactImpulse) = Unit

    companion object {
        private const val stepRate = 1 / 45f
    }
}
