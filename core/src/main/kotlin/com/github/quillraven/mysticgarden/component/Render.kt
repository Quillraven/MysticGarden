package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Render(val sprite: Sprite) : Component<Render> {
    override fun type(): ComponentType<Render> = Render

    companion object : ComponentType<Render>()
}
