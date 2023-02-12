package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.mysticgarden.component.Remove

class RemoveSystem : IteratingSystem(family { all(Remove) }) {

    override fun onTickEntity(entity: Entity) {
        with(entity[Remove]) {
            if (delay <= 0f) {
                entity.remove()
                return
            }

            delay -= deltaTime
        }
    }
}