package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Disable(var time: Float) : Component<Disable> {
    override fun type(): ComponentType<Disable> = Disable

    companion object : ComponentType<Disable>()
}