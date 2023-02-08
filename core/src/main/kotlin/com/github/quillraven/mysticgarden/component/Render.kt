package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Render(val sprite: Sprite) : Component<Render> {
    override fun type(): ComponentType<Render> = Render

    companion object : ComponentType<Render>()
}

operator fun Sprite.component1(): Float = this.x

operator fun Sprite.component2(): Float = this.y

operator fun Sprite.component3(): Float = this.width

operator fun Sprite.component4(): Float = this.height
