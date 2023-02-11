package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Player(var crystals: Int = 0, var chromas: Int = 0) : Component<Player> {
    override fun type(): ComponentType<Player> = Player

    companion object : ComponentType<Player>()
}