package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.mysticgarden.RegionName
import com.github.quillraven.mysticgarden.event.Trigger
import ktx.app.gdxError

enum class TiledObjectType(
    val regionName: RegionName,
    val isDestructible: Boolean = false,
    val item: ItemType? = null,
) {
    TUTORIAL_TREE(RegionName.TREE_GREEN),
    CRYSTAL(RegionName.CRYSTAL),
    TREE(RegionName.TREE_RED, isDestructible = true),
    FIRE_STONE(RegionName.FIRE_WALL, isDestructible = true),
    AXE(RegionName.AXE, item = ItemType.AXE),
    TORCH(RegionName.TORCH),
    FIRE(RegionName.ALTAR),
    ORB(RegionName.ORB),
    WALL(RegionName.WALL, isDestructible = true),
    PORTAL(RegionName.PORTAL),
    WAND(RegionName.WAND, item = ItemType.WAND),
    CLUB(RegionName.CLUB, item = ItemType.CLUB),
    BOOTS(RegionName.BOOTS, item = ItemType.BOOTS);

    companion object {
        fun of(name: String): TiledObjectType {
            return entries.firstOrNull { it.name == name.uppercase() } ?: gdxError("Unknown object type: $name")
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