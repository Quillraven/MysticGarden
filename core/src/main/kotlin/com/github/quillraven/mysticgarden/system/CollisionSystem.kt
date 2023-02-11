package com.github.quillraven.mysticgarden.system

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
) : IntervalSystem(enabled = false) {

    init {
        eventDispatcher.register(::onPlayerEntityCollision)
    }

    override fun onTick() = Unit

    private fun onPlayerEntityCollision(event: PlayerCollisionEvent) {
        val (player, other) = event

        if (other hasNo Tiled) {
            return
        }

        val (_, type) = other[Tiled]
        when (type) {
            TiledObjectType.CRYSTAL -> {
                player[Player].crystals++
                other.remove()
            }

            else -> log.debug { "Collision with $type not handled" }
        }
    }

    companion object {
        private val log = Logger(CollisionSystem::class.java.simpleName)
    }
}