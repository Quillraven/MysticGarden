package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Tiled(val id: Int) : Component<Tiled> {
    override fun type(): ComponentType<Tiled> = Tiled

    companion object : ComponentType<Tiled>()
}