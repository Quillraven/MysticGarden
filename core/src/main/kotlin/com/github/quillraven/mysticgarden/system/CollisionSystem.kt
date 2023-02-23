package com.github.quillraven.mysticgarden.system

import box2dLight.RayHandler
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.SoundAsset
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.component.*
import com.github.quillraven.mysticgarden.event.CrystalPickupEvent
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.OrbPickupEvent
import ktx.graphics.component1
import ktx.graphics.component2
import ktx.graphics.component3
import ktx.log.Logger

class CollisionSystem(
    private val rayHandler: RayHandler = inject(),
    private val audioService: AudioService = inject(),
    private val eventDispatcher: EventDispatcher = inject(),
) : IteratingSystem(family { all(Collision, Player) }) {

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
            TiledObjectType.CRYSTAL -> {
                val p = player[Player]
                p.crystals++
                audioService.play(SoundAsset.COLLECT)
                eventDispatcher.dispatch(CrystalPickupEvent(p.crystals))
            }

            TiledObjectType.ORB -> {
                val p = player[Player]
                p.chromas++

                val (r, g, b) = Light.ambientColor
                val gain = p.chromas * Light.ambientOrbGain
                rayHandler.setAmbientLight(r + gain, g + gain, b + gain, 1f)
                audioService.play(SoundAsset.JINGLE)

                eventDispatcher.dispatch(OrbPickupEvent(p.chromas))
            }

            TiledObjectType.AXE -> {
                player[Player].items.add(ItemType.AXE)
                audioService.play(SoundAsset.JINGLE)
            }

            TiledObjectType.CLUB -> {
                player[Player].items.add(ItemType.CLUB)
                audioService.play(SoundAsset.JINGLE)
            }

            TiledObjectType.WAND -> {
                player[Player].items.add(ItemType.WAND)
                audioService.play(SoundAsset.JINGLE)
            }

            TiledObjectType.TREE -> {
                if (player hasItem ItemType.AXE) {
                    audioService.play(SoundAsset.CHOP)
                    return true
                }
                return false
            }

            TiledObjectType.WALL -> {
                if (player hasItem ItemType.CLUB) {
                    audioService.play(SoundAsset.SMASH)
                    return true
                }
                return false
            }

            TiledObjectType.FIRE_STONE -> {
                if (player hasItem ItemType.WAND) {
                    audioService.play(SoundAsset.SWING)
                    return true
                }
                return false
            }

            TiledObjectType.PORTAL -> {
                val p = player[Player]
                if (p.crystals >= p.maxCrystals) {
                    // TODO change to victory screen
                    println("VICTORY")
                }
                // don't destroy the Portal ;)
                return false
            }

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