package com.github.quillraven.mysticgarden

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
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
    val physicWorld = this.inject<PhysicWorld>()

    return when (name) {
        "START_LOCATION" -> {
            this.entity {
                it += Player()
                it += CameraLock()
                it += Move(3f)
                it += Boundary(x, y, 0.8f, 0.8f)
                it += Physic.of(physicWorld, it[Boundary], BodyType.DynamicBody, it)
                it += Animation.of(assets, RegionName.HERO_UP)
                it += Render(sprite(x, y, it[Animation].firstFrame, 1.5f, 1.5f))
            }
        }

        else -> {
            this.entity {
                val type = TiledObjectType.of(name)

                if (mapObject !is TiledMapTileMapObject) {
                    gdxError("Unsupported MapObject for $type")
                } else if (mapObject.tile.objects.count != 1) {
                    // To keep it simple we only support a single collision object
                    gdxError("Not exactly one tile object detected for $type")
                }
                val tileObject = mapObject.tile.objects.first()
                if (tileObject !is RectangleMapObject) {
                    gdxError("Unsupported tile object $tileObject for $type")
                }

                val (_, _, objW, objH) = tileObject.rectangle
                it += Tiled(tiledId, type)
                it += Boundary(x, y, objW, objH, Layer.BACKGROUND)
                it += Physic.of(physicWorld, it[Boundary], BodyType.StaticBody, it)

                val objectRegionName = type.regionName
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
