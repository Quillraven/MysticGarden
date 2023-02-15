package com.github.quillraven.mysticgarden.component

import box2dLight.ConeLight
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
    val angle: ClosedFloatingPointRange<Float> = 0f..0f,
    var distanceTime: Float = 0f,
    var distanceDirection: Int = -1,
) : Component<Light> {

    override fun type() = Light

    companion object : ComponentType<Light>() {
        private const val numRays = 64
        val distanceInterpolation: Interpolation = Interpolation.smoother
        val angleInterpolation: Interpolation = Interpolation.swing

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
            val (x, y) = boundary

            return Light(
                PointLight(rayHandler, numRays, color, distance.endInclusive, x, y).apply {
                    attachToBody(body)
                    // softness length allows the light to go through objects.
                    // E.g. this makes the player light illuminate trees when he is close to them.
                    setSoftnessLength(3.5f)
                },
                distance
            )
        }

        fun coneLightOf(
            rayHandler: RayHandler,
            color: Color,
            distance: ClosedFloatingPointRange<Float>,
            angle: ClosedFloatingPointRange<Float>,
            direction: Float,
            boundary: Boundary,
            body: Body
        ): Light {
            val (x, y, _, h) = boundary

            return Light(
                ConeLight(rayHandler, numRays, color, distance.endInclusive, x, y, 0f, angle.endInclusive).apply {
                    attachToBody(body, 0f, h, direction)
                    setSoftnessLength(3.5f)
                },
                distance,
                angle
            )
        }
    }
}