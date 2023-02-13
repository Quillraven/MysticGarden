package com.github.quillraven.mysticgarden.component

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentHook
import com.github.quillraven.fleks.ComponentType

typealias B2DLight = box2dLight.Light

data class Light(
    val light: B2DLight,
    val distance: ClosedFloatingPointRange<Float>,
    var distanceTime: Float = 0f,
    var distanceDirection: Int = -1,
) : Component<Light> {

    override fun type() = Light

    companion object : ComponentType<Light>() {
        private const val defaultRays = 64
        val distanceInterpolation: Interpolation = Interpolation.smoother

        val onRemove: ComponentHook<Light> = { _, component: Light ->
            component.light.remove()
        }

        fun pointLightOf(
            rayHandler: RayHandler,
            color: Color,
            distance: ClosedFloatingPointRange<Float>,
            boundary: Boundary,
            body: Body
        ): Light {
            val (x, y, w, h) = boundary

            return Light(
                PointLight(rayHandler, defaultRays, color, distance.endInclusive, x, y).apply {
                    attachToBody(body, w * 0.5f, h * 0.5f)
                    setSoftnessLength(3.5f)
                },
                distance
            )
        }
    }
}