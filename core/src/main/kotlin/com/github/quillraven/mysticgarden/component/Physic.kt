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
import ktx.box2d.circle
import ktx.math.vec2

data class Physic(
    val body: Body,
    val posBeforeStep: Vector2 = vec2(body.position.x, body.position.y),
) : Component<Physic> {
    override fun type(): ComponentType<Physic> = Physic

    companion object : ComponentType<Physic>() {
        fun of(world: PhysicWorld, boundary: Boundary, bodyType: BodyType, entity: Entity, category: Short): Physic {
            val (x, y, w, h) = boundary

            return Physic(
                world.body(bodyType) {
                    userData = entity
                    position.set(x + w * 0.5f, y + h * 0.5f)
                    fixedRotation = true

                    if (bodyType == BodyType.StaticBody) {
                        box(w, h) { filter.categoryBits = category }
                    } else {
                        // we use CircleShape to avoid the ghost vertices problem
                        // and to not get stuck so easily with the terrain
                        circle(w * 0.5f) { filter.categoryBits = category }
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
