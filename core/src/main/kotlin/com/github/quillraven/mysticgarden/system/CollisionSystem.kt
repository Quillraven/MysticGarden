package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.ItemType
import com.github.quillraven.mysticgarden.component.Player
import com.github.quillraven.mysticgarden.component.Tiled
import com.github.quillraven.mysticgarden.component.TiledObjectType
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.PlayerCollisionEvent
import ktx.log.Logger

class CollisionSystem(
    eventDispatcher: EventDispatcher = inject(),
) : IntervalSystem(enabled = false) {

    init {
        eventDispatcher.register(::onPlayerEntityCollision)
    }

    override fun onTick() = Unit

    private infix fun Entity.hasItem(type: ItemType): Boolean = type in this[Player].items

    private fun onPlayerEntityCollision(event: PlayerCollisionEvent) {
        val (player, other) = event

        if (other hasNo Tiled) {
            return
        }

        val (_, type, trigger) = other[Tiled]

        // run trigger if Tiled entity is linked to a trigger
        trigger?.let {
            log.debug { "Running trigger $trigger" }
            it.action(world)
        }

        // handle remaining collision logic with Tiled entity
        when (type) {
            TiledObjectType.CRYSTAL -> {
                player[Player].crystals++
                other.remove()
            }

            TiledObjectType.AXE -> {
                player[Player].items.add(ItemType.AXE)
                other.remove()
            }

            TiledObjectType.TREE -> {
                if (player hasItem ItemType.AXE) {
                    other.remove()
                }
            }

            TiledObjectType.CLUB -> {
                player[Player].items.add(ItemType.CLUB)
                other.remove()
            }

            TiledObjectType.WALL -> {
                if (player hasItem ItemType.CLUB) {
                    other.remove()
                }
            }

            TiledObjectType.WAND -> {
                player[Player].items.add(ItemType.WAND)
                other.remove()
            }

            TiledObjectType.FIRE_STONE -> {
                if (player hasItem ItemType.WAND) {
                    other.remove()
                }
            }

            else -> log.debug { "Collision with $type not handled" }
        }
    }

    companion object {
        private val log = Logger(CollisionSystem::class.java.simpleName)
    }
}