package com.github.quillraven.mysticgarden

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.component.*
import com.github.quillraven.mysticgarden.system.component1
import com.github.quillraven.mysticgarden.system.component2
import com.github.quillraven.mysticgarden.system.component3
import com.github.quillraven.mysticgarden.system.component4
import ktx.app.gdxError

fun World.spawnObject(mapObject: MapObject): Entity {
    val (name, x, y, tiledId) = mapObject
    val assets = this.inject<Assets>()

    return when (name) {
        "START_LOCATION" -> {
            this.entity {
                it += Player()
                it += CameraLock()
                it += Boundary(x, y, 1f, 1f)
                it += Animation.of(assets, RegionName.HERO_UP)
                it += Render(sprite(x, y, it[Animation].firstFrame, 1.5f, 1.5f))
            }
        }

        else -> {
            this.entity {
                it += Tiled(tiledId)
                it += Boundary(x, y, 1f, 1f)
                val objectRegionName = objectRegionName(name)
                if (objectRegionName.isAnimation) {
                    it += Animation.of(assets, objectRegionName)
                    it += Render(sprite(x, y, it[Animation].firstFrame))
                } else {
                    it += Render(sprite(x, y, assets[objectRegionName]))
                }
            }
        }
    }
}

private fun sprite(x: Float, y: Float, region: TextureRegion, width: Float = 1f, height: Float = 1f) =
    Sprite(region).apply {
        setBounds(x, y, width, height)
        setOriginCenter()
    }

private fun objectRegionName(name: String): RegionName = when (name) {
    "Tutorial_Tree" -> RegionName.MANGROVE
    "Crystal" -> RegionName.CRYSTAL
    "Tree" -> RegionName.TREE_1_RED
    "Fire_Stone" -> RegionName.CRYSTAL_WALL_LIGHTRED
    "Axe" -> RegionName.AXE
    "Torch" -> RegionName.TORCH
    "Fire" -> RegionName.ALTAR_MAKHLEB_FLAME
    "Orb" -> RegionName.CHROMA_ORB
    "Wall" -> RegionName.STONE_BRICK
    "Portal" -> RegionName.PORTAL
    "Wand" -> RegionName.URAND_FIRESTARTER
    "Club" -> RegionName.GIANT_SPIKED_CLUB
    else -> gdxError("Unknown object type: $name")
}
