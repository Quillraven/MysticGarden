package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.mysticgarden.component.*
import ktx.log.Logger

class CollisionSystem : IteratingSystem(family { all(Collision, Player) }) {

    private infix fun Entity.hasItem(type: ItemType): Boolean = type in this[Player].items

    override fun onTickEntity(entity: Entity) {
        entity[Collision].entities
            .filter { it has Tiled }
            .forEach { other ->
                val (_, type, trigger) = other[Tiled]

                // run trigger if Tiled entity is linked to a trigger
                trigger?.let {
                    log.debug { "Running trigger $trigger" }
                    it.action(world)
                }

                // handle remaining collision logic with Tiled entity
                if (onTiledCollision(entity, type)) {
                    // collision handled -> remove other entity
                    other.remove()
                }
            }

        entity.configure { it -= Collision }
    }

    private fun onTiledCollision(player: Entity, type: TiledObjectType): Boolean {
        when (type) {
            TiledObjectType.CRYSTAL -> player[Player].crystals++
            TiledObjectType.AXE -> player[Player].items.add(ItemType.AXE)
            TiledObjectType.CLUB -> player[Player].items.add(ItemType.CLUB)
            TiledObjectType.WAND -> player[Player].items.add(ItemType.WAND)
            TiledObjectType.TREE -> return player hasItem ItemType.AXE
            TiledObjectType.WALL -> return player hasItem ItemType.CLUB
            TiledObjectType.FIRE_STONE -> return player hasItem ItemType.WAND
            else -> {
                log.debug { "Collision with $type not handled" }
                return false
            }
        }

        return true
    }

    companion object {
        private val log = Logger(CollisionSystem::class.java.simpleName)
    }
}