package com.github.quillraven.mysticgarden.system

import box2dLight.RayHandler
import com.badlogic.gdx.graphics.OrthographicCamera
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.Light

class LightSystem(
    private val gameCamera: OrthographicCamera = inject(),
    private val rayHandler: RayHandler = inject(),
) : IteratingSystem(family { all(Light) }) {

    override fun onTick() {
        super.onTick()

        rayHandler.setCombinedMatrix(gameCamera)
        rayHandler.updateAndRender()
    }

    override fun onTickEntity(entity: Entity) {
        val light = entity[Light]
        val (b2dLight, distance, time, direction) = light

        light.distanceTime = (time + direction * deltaTime).coerceIn(0f, 1f)
        if (light.distanceTime == 0f || light.distanceTime == 1f) {
            light.distanceDirection *= -1
        }
        b2dLight.distance = Light.distanceInterpolation.apply(distance.start, distance.endInclusive, light.distanceTime)
    }
}