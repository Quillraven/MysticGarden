package com.github.quillraven.mysticgarden

import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateContext
import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.component.*
import com.github.quillraven.mysticgarden.system.*
import ktx.app.gdxError

fun World.spawnPlayer(x: Float, y: Float): Entity {
    val assets = this.inject<Assets>()
    val physicWorld = this.inject<PhysicWorld>()
    val rayHandler = this.inject<RayHandler>()

    return this.entity {
        it += Player()
        it += CameraLock()
        it += Move(3f)
        val boundary = Boundary(x, y, 0.8f, 0.8f)
        it += boundary
        it += Physic.of(physicWorld, boundary, BodyType.DynamicBody, it, MysticGarden.b2dPlayer)
        it += Light.pointLightOf(rayHandler, Color(1f, 1f, 1f, 0.7f), 5f..6.5f, boundary, it[Physic].body)
        it += Animation.of(assets, RegionName.HERO_DOWN, 0f)
        it += Render(sprite(x, y, it[Animation].firstFrame, 1.5f, 1.5f))
    }
}

fun World.spawnObject(mapObject: MapObject): Entity {
    val (name, x, y, _, _, tiledId) = mapObject
    val assets = this.inject<Assets>()
    val physicWorld = this.inject<PhysicWorld>()
    val rayHandler = this.inject<RayHandler>()

    return when (val type = TiledObjectType.of(name)) {
        TiledObjectType.CRYSTAL -> mapObjEntity(type, mapObject, tiledId, x, y, physicWorld, assets) {
            it += Light.pointLightOf(rayHandler, Color.SKY, 2f..2.5f, it[Boundary], it[Physic].body)
        }

        TiledObjectType.FIRE -> mapObjEntity(type, mapObject, tiledId, x, y, physicWorld, assets) {
            it += Light.pointLightOf(rayHandler, Color.CORAL, 1.5f..2f, it[Boundary], it[Physic].body)
        }

        else -> mapObjEntity(type, mapObject, tiledId, x, y, physicWorld, assets)
    }
}

private fun World.mapObjEntity(
    type: TiledObjectType,
    mapObject: MapObject,
    tiledId: Int,
    x: Float,
    y: Float,
    physicWorld: PhysicWorld,
    assets: Assets,
    extraCfg: EntityCreateContext.(Entity) -> Unit = {},
) = this.entity {

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

    val (_, _, objW, objH) = tileObject.rectangle.scl(MysticGarden.unitScale)
    it += Tiled(tiledId, type, mapObject.trigger)
    it += Boundary(x, y, objW, objH, Layer.BACKGROUND)
    it += Physic.of(physicWorld, it[Boundary], BodyType.StaticBody, it, MysticGarden.b2dMapObject)

    val objectRegionName = type.regionName
    if (objectRegionName.isAnimation) {
        it += Animation.of(assets, objectRegionName)
        it += Render(sprite(x, y, it[Animation].firstFrame))
    } else {
        it += Render(sprite(x, y, assets[objectRegionName]))
    }

    extraCfg(this, it)
}

private fun sprite(x: Float, y: Float, region: TextureRegion, width: Float = 1f, height: Float = 1f) =
    Sprite(region).apply {
        setBounds(x, y, width, height)
        setOriginCenter()
    }
