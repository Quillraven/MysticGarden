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
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.Move
import com.github.quillraven.mysticgarden.component.Physic
import com.github.quillraven.mysticgarden.component.Player
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.PlayerCollisionEvent
import ktx.math.component1
import ktx.math.component2

class PhysicSystem(
    private val physicWorld: PhysicWorld = inject(),
    private val eventDispatcher: EventDispatcher = inject(),
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
        physicMoveEntities.forEach {
            val (_, speed) = it[Move]
            val (body) = it[Physic]
            val (worldX, worldY) = body.worldCenter
            val (velX, velY) = body.linearVelocity

            body.applyLinearImpulse(
                body.mass * speed.x - velX,
                body.mass * speed.y - velY,
                worldX, worldY,
                true
            )
        }

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
        val (body, posBeforeStep) = entity[Physic]
        posBeforeStep.set(body.position)
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

    override fun beginContact(contact: Contact) {
        val dataA = contact.fixtureA.body.userData
        val dataB = contact.fixtureB.body.userData

        if (dataA !is Entity || dataB !is Entity) {
            return
        }

        if (dataA has Player) {
            eventDispatcher.dispatch(PlayerCollisionEvent(dataA, dataB))
        } else if (dataB has Player) {
            eventDispatcher.dispatch(PlayerCollisionEvent(dataB, dataA))
        }
    }

    override fun endContact(contact: Contact) = Unit

    override fun preSolve(contact: Contact, oldManifold: Manifold) = Unit

    override fun postSolve(contact: Contact, impulse: ContactImpulse) = Unit

    companion object {
        private const val stepRate = 1 / 60f
    }
}
