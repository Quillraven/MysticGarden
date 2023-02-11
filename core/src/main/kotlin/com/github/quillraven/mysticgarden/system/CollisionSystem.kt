package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.Player
import com.github.quillraven.mysticgarden.component.Tiled
import com.github.quillraven.mysticgarden.component.TiledObjectType
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.PlayerCollisionEvent
import ktx.log.Logger

// To prevent the ghost-vertices issue, our player uses EdgeShapes in Box2D
// which causes a contact event for each edge. This means that sometimes
// the two or more contacts are triggered when in fact the player is just
// colliding with a single object.
// The CollisionSystem makes sure that multiple collisions in the same frame
// with a single entity are only handled once.
class CollisionSystem(
    eventDispatcher: EventDispatcher = inject(),
) : IntervalSystem() {

    private val alreadyProcessed = mutableListOf<Entity>()

    init {
        eventDispatcher.register(::onPlayerEntityCollision)
    }

    override fun onTick() {
        alreadyProcessed.forEach {
            if (it[Tiled].remove) {
                it.remove()
            }
        }
        alreadyProcessed.clear()
    }

    private fun onPlayerEntityCollision(event: PlayerCollisionEvent) {
        val (player, other) = event

        if (other hasNo Tiled || other in alreadyProcessed) {
            return
        }

        alreadyProcessed += other
        val tiled = other[Tiled]

        // We cannot remove entities directly in this method
        // because we are in the middle of a Box2D contact event.
        // That's why we set a 'remove' flag to delay the removal.
        when (tiled.type) {
            TiledObjectType.CRYSTAL -> {
                player[Player].crystals++
                tiled.remove = true
            }

            else -> log.debug { "Collision with ${tiled.type} not handled" }
        }
    }

    companion object {
        private val log = Logger(CollisionSystem::class.java.simpleName)
    }
}