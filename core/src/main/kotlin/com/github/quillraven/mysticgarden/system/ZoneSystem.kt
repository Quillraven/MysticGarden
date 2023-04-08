package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.Disable
import com.github.quillraven.mysticgarden.component.Player
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.event.ZoneChangeEvent
import ktx.app.gdxError
import ktx.collections.GdxArray
import ktx.collections.addAll
import ktx.math.vec2
import ktx.tiled.id

data class Zone(val id: Int, val rect: Rectangle) : Shape2D by rect {
    val isEmpty: Boolean
        get() = rect.width == 0f

    val isNotEmpty: Boolean
        get() = rect.width != 0f
}

class ZoneSystem(
    private val eventDispatcher: EventDispatcher = inject()
) : IteratingSystem(family { all(Player, Boundary) }) {

    private lateinit var activeMap: TiledMap
    var activeZone = defaultZone
        private set
    private val allZones = GdxArray<Zone>()
    private val tmpVec2 = vec2()

    init {
        eventDispatcher.register<MapChangeEvent> { (map) ->
            // init initial zone of new map
            activeMap = map
            allZones.clear()
            allZones.addAll(
                map.zoneLayer.objects
                    .filterIsInstance<RectangleMapObject>()
                    .map {
                        val (x, y, w, h) = it.rectangle.scl(MysticGarden.unitScale)
                        Zone(it.id, Rectangle(x, y, w, h))
                    }
            )

            val (_, startX, startY) = map.startLocation
            updateActiveZone(vec2(startX, startY))
        }
    }

    private fun updateActiveZone(position: Vector2) {
        val prevZone = activeZone.copy()

        activeZone = allZones.firstOrNull { position in it }
            ?: gdxError("No zone contains $position")

        eventDispatcher.dispatch(ZoneChangeEvent(position, activeMap, activeZone, prevZone))
    }

    private fun Boundary.center(): Vector2 {
        val (x, y, w, h) = this
        return tmpVec2.set(x + w * 0.5f, y + h * 0.5f)
    }

    override fun onTickEntity(entity: Entity) {
        val center = entity[Boundary].center()

        if (center !in activeZone) {
            updateActiveZone(center)
            entity.configure { it += Disable(CameraSystem.maxPanTime) }
        }
    }

    fun resetActiveZone(playerPosition: Vector2) {
        activeZone = defaultZone
        updateActiveZone(playerPosition)
    }

    companion object {
        private val defaultZone = Zone(-1, Rectangle())
    }
}