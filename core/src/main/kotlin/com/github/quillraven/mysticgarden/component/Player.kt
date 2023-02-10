package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class Player : Component<Player> {
    override fun type(): ComponentType<Player> = Player

    companion object : ComponentType<Player>()
}