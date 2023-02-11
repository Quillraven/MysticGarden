package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
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
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

class ZoneSystem(
    private val eventDispatcher: EventDispatcher = inject()
) : IteratingSystem(family { all(Player, Boundary) }) {

    private val activeZone = Rectangle()
    private val allZones = GdxArray<Rectangle>()
    private val tmpVec2 = vec2()

    init {
        eventDispatcher.register<MapChangeEvent> { (map) ->
            // init initial zone
            allZones.addAll(
                map.zoneLayer.objects
                    .filterIsInstance<RectangleMapObject>()
                    .map {
                        val (x, y, w, h) = it.rectangle.scl(MysticGarden.unitScale)
                        Rectangle(x, y, w, h)
                    }
            )

            val (startX, startY) = map.startLocation
            updateActiveZone(startX, startY)
        }
    }

    private fun updateActiveZone(x: Float, y: Float) {
        val prevZone = Rectangle(activeZone)

        activeZone.set(
            allZones.firstOrNull { it.contains(x, y) }
                ?: gdxError("No zone contains ($x, $y)")
        )

        eventDispatcher.dispatch(ZoneChangeEvent(activeZone, prevZone))
    }

    private fun Boundary.center(): Vector2 {
        val (x, y, w, h) = this
        return tmpVec2.set(x + w * 0.5f, y + h * 0.5f)
    }

    override fun onTickEntity(entity: Entity) {
        val (centerX, centerY) = entity[Boundary].center()

        if (!activeZone.contains(centerX, centerY)) {
            updateActiveZone(centerX, centerY)
            entity.configure { it += Disable(CameraSystem.maxPanTime) }
        }
    }
}