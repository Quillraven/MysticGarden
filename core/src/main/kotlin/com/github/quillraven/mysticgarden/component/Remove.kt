package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Remove(var delay: Float) : Component<Remove> {
    override fun type() = Remove

    companion object : ComponentType<Remove>()
}