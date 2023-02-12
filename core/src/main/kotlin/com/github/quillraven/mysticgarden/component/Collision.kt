package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import ktx.collections.GdxSet

data class Collision(val entities: GdxSet<Entity> = GdxSet()) : Component<Collision> {
    override fun type() = Collision

    companion object : ComponentType<Collision>()
}