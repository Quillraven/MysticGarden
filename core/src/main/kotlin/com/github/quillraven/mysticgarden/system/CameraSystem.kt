package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.CameraLock
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.ZoneChangeEvent
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import kotlin.math.max
import kotlin.math.min

class CameraSystem(
    private val gameCamera: OrthographicCamera = inject(),
    eventDispatcher: EventDispatcher = inject(),
) : IteratingSystem(family { all(CameraLock, Boundary) }) {

    private val zone = Rectangle()
    private val boundaries = Rectangle()

    private var pan = false
    private var panTime = 0f
    private val panTo = vec2()
    private val panFrom = vec2()

    init {
        eventDispatcher.register<ZoneChangeEvent> { (newZone, oldZone) ->
            val (x, y, w, h) = newZone
            zone.set(x, y, x + w, y + h)

            if (oldZone.width == 0f) {
                // first time a zone is set -> do not start a pan
                return@register
            }

            startPan()
        }
    }

    private fun startPan() {
        val (minW, maxW, minH, maxH) = camBoundaries()
        val (camX, camY) = gameCamera

        pan = true
        panTime = 0f
        panFrom.set(camX, camY)
        panTo.set(camX.coerceIn(minW, maxW), camY.coerceIn(minH, maxH))
    }

    private fun camBoundaries(): Rectangle {
        val (zoneX, zoneY, zoneW, zoneH) = zone
        val (_, _, _, camW, camH) = gameCamera

        return boundaries.set(
            min(zoneX + camW, zoneW - camW),
            max(zoneX + camW, zoneW - camW),
            min(zoneY + camH, zoneH - camH),
            max(zoneY + camH, zoneH - camH)
        )
    }

    override fun onTick() {
        if (pan) {
            // pan camera
            val (fromX, fromY) = panFrom
            val (toX, toY) = panTo
            panTime = (panTime + deltaTime / maxPanTime).coerceAtMost(1f)
            pan = panTime < 1f

            gameCamera.position.set(
                panInterpolation.apply(fromX, toX, panTime),
                panInterpolation.apply(fromY, toY, panTime),
                gameCamera.position.z
            )
            return
        }

        super.onTick()
    }

    override fun onTickEntity(entity: Entity) {
        val (entityX, entityY, entityW, entityH) = entity[Boundary]
        val (minW, maxW, minH, maxH) = camBoundaries()

        gameCamera.position.set(
            (entityX + entityW * 0.5f).coerceIn(minW, maxW),
            (entityY + entityH * 0.5f).coerceIn(minH, maxH),
            gameCamera.position.z
        )
    }

    companion object {
        const val maxPanTime = 1.2f
        private val panInterpolation = Interpolation.pow2In
    }
}

private operator fun Camera.component1(): Float = this.position.x

private operator fun Camera.component2(): Float = this.position.y

private operator fun Camera.component3(): Float = this.position.z

private operator fun Camera.component4(): Float = this.viewportWidth * 0.5f

private operator fun Camera.component5(): Float = this.viewportHeight * 0.5f
