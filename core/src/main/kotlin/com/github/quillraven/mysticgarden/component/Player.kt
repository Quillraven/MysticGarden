package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.collections.GdxArray

enum class ItemType {
    AXE, CLUB, WAND
}

data class Player(
    var crystals: Int = 0,
    var chromas: Int = 0,
    val items: GdxArray<ItemType> = GdxArray()
) : Component<Player> {

    var maxCrystals = 0

    override fun type(): ComponentType<Player> = Player

    companion object : ComponentType<Player>()
}