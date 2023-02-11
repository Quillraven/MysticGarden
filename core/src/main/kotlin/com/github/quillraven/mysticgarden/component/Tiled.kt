package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.mysticgarden.RegionName
import com.github.quillraven.mysticgarden.event.Trigger
import ktx.app.gdxError

enum class TiledObjectType(val regionName: RegionName) {
    TUTORIAL_TREE(RegionName.MANGROVE),
    CRYSTAL(RegionName.CRYSTAL),
    TREE(RegionName.TREE_1_RED),
    FIRE_STONE(RegionName.CRYSTAL_WALL_LIGHTRED),
    AXE(RegionName.AXE),
    TORCH(RegionName.TORCH),
    FIRE(RegionName.ALTAR_MAKHLEB_FLAME),
    ORB(RegionName.CHROMA_ORB),
    WALL(RegionName.STONE_BRICK),
    PORTAL(RegionName.PORTAL),
    WAND(RegionName.URAND_FIRESTARTER),
    CLUB(RegionName.GIANT_SPIKED_CLUB);

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