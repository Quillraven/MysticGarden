package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.mysticgarden.RegionName
import com.github.quillraven.mysticgarden.event.Trigger
import ktx.app.gdxError

enum class TiledObjectType(val regionName: RegionName) {
    TUTORIAL_TREE(RegionName.TREE_GREEN),
    CRYSTAL(RegionName.CRYSTAL),
    TREE(RegionName.TREE_RED),
    FIRE_STONE(RegionName.FIRE_WALL),
    AXE(RegionName.AXE),
    TORCH(RegionName.TORCH),
    FIRE(RegionName.ALTAR),
    ORB(RegionName.ORB),
    WALL(RegionName.WALL),
    PORTAL(RegionName.PORTAL),
    WAND(RegionName.WAND),
    CLUB(RegionName.CLUB);

    companion object {
        fun of(name: String): TiledObjectType {
            return values().firstOrNull { it.name == name.uppercase() } ?: gdxError("Unknown object type: $name")
        }
    }
}

data class Tiled(
    val id: Int,
    val type: TiledObjectType,
    val trigger: Trigger? = null,
) : Component<Tiled> {
    override fun type(): ComponentType<Tiled> = Tiled

    companion object : ComponentType<Tiled>()
}