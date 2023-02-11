package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentHook
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.mysticgarden.PhysicWorld
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.edge
import ktx.math.vec2

data class Physic(
    val body: Body,
    val posBeforeStep: Vector2 = vec2(),
) : Component<Physic> {
    override fun type(): ComponentType<Physic> = Physic

    companion object : ComponentType<Physic>() {
        fun of(world: PhysicWorld, boundary: Boundary, bodyType: BodyType, entity: Entity): Physic {
            val (x, y, w, h) = boundary

            return Physic(
                world.body(bodyType) {
                    userData = entity
                    position.set(x, y)
                    fixedRotation = true

                    if (bodyType == BodyType.StaticBody) {
                        box(w, h, vec2(w * 0.5f, h * 0.5f))
                    } else {
                        // we use EdgeShape since it has a built-in ghost vertices
                        // prevention mechanism in LibGDX since version 1.1.0
                        edge(0f, 0f, w, 0f)
                        edge(w, 0f, w, h)
                        edge(w, h, 0f, h)
                        edge(0f, h, 0f, 0f)
                    }
                }
            )
        }

        val onRemove: ComponentHook<Physic> = { _: Entity, component: Physic ->
            val (body) = component
            body.world.destroyBody(body)
            body.userData = null
        }
    }
}