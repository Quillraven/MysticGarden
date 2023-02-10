package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.mysticgarden.PhysicWorld
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.loop
import ktx.math.vec2

data class Physic(
    val body: Body,
    val posBeforeStep: Vector2 = vec2(),
) : Component<Physic> {
    override fun type(): ComponentType<Physic> = Physic

    companion object : ComponentType<Physic>() {
        fun of(world: PhysicWorld, boundary: Boundary, bodyType: BodyType): Physic {
            val (x, y, w, h) = boundary

            return Physic(
                world.body(bodyType) {
                    position.set(x, y)

                    if (bodyType == BodyType.StaticBody) {
                        box(w, h, vec2(w * 0.5f, h * 0.5f))
                    } else {
                        // DynamicBody will use a chain shape instead
                        // of a box to avoid ghost vertices issue
                        loop(
                            vec2(0f, 0f),
                            vec2(w, 0f),
                            vec2(w, h),
                            vec2(0f, h)
                        )
                    }

                }
            )
        }
    }
}