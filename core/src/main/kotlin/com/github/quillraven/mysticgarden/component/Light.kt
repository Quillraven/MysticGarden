package com.github.quillraven.mysticgarden.component

import box2dLight.ConeLight
import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.physics.box2d.Body
import com.github.quillraven.fleks.*

typealias B2DLight = box2dLight.Light

data class Light(
    val light: B2DLight,
    val distance: ClosedFloatingPointRange<Float>,
    val angle: ClosedFloatingPointRange<Float> = 0f..0f,
    var distanceTime: Float = 0f,
    var distanceDirection: Int = -1,
) : Component<Light> {

    private val cmpType = if (light is PointLight) LightPoint else LightCone

    override fun type(): ComponentType<Light> = cmpType

    override fun World.onRemove(entity: Entity) {
        light.remove()
    }

    companion object {
        val LightPoint = componentTypeOf<Light>()
        val LightCone = componentTypeOf<Light>()

        private const val numRays = 64
        val ambientColor = Color(0.05f, 0.05f, 0.05f, 1f)
        const val ambientOrbGain = 0.02f
        val distanceInterpolation: Interpolation = Interpolation.smoother
        val angleInterpolation: Interpolation = Interpolation.swing

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
            val (x, y, w, h) = boundary

            return Light(
                ConeLight(rayHandler, numRays, color, distance.endInclusive, x, y, 0f, angle.endInclusive).apply {
                    attachToBody(body, w * 0.5f, h, direction)
                    setSoftnessLength(3.5f)
                },
                distance,
                angle
            )
        }
    }
}