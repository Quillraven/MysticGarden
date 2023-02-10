package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

enum class Layer(val z: Int) {
    BACKGROUND(-1),
    DEFAULT(0),
    FOREGROUND(1),
}

data class Boundary(
    var x: Float,
    var y: Float,
    val width: Float = 1f,
    val height: Float = 1f,
    val layer: Layer = Layer.DEFAULT,
) : Component<Boundary>, Comparable<Boundary> {

    override fun type(): ComponentType<Boundary> = Boundary

    override fun compareTo(other: Boundary): Int = when {
        layer.z < other.layer.z -> -1
        layer.z > other.layer.z -> 1
        y < other.y -> -1
        y > other.y -> 1
        x < other.x -> -1
        x > other.x -> 1
        else -> 0
    }

    companion object : ComponentType<Boundary>()
}
