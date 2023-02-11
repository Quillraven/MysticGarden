package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.mysticgarden.component.Disable

class DisableSystem : IteratingSystem(family { all(Disable) }) {

    override fun onTickEntity(entity: Entity) {
        with(entity[Disable]) {
            time -= deltaTime
            if (time <= 0f) {
                entity.configure { it -= Disable }
            }
        }
    }
}