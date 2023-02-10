package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.component.Animation
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.spawnObject
import ktx.app.gdxError
import ktx.tiled.forEachMapObject
import ktx.tiled.id
import ktx.tiled.x
import ktx.tiled.y

class MapSystem(
    eventDispatcher: EventDispatcher = inject(),
) : IntervalSystem() {

    init {
        eventDispatcher.register(this::onMapChange)
    }

    override fun onTick() {
        AnimatedTiledMapTile.updateAnimationBaseTime()
    }

    private fun onMapChange(event: MapChangeEvent) {
        spawnObjects(event.map)
    }

    private val MapObject.animatedTile: AnimatedTiledMapTile?
        get() {
            if (this is TiledMapTileMapObject && this.tile is AnimatedTiledMapTile) {
                return this.tile as AnimatedTiledMapTile
            }
            return null
        }

    private fun spawnObjects(map: TiledMap) {
        map.forEachMapObject("objects") { mapObj ->
            val entity = world.spawnObject(mapObj)
            if (entity has Animation) {
                // adjust the animation speed to the speed defined in Tiled mapeditor
                val animatedTile = mapObj.animatedTile ?: return@forEachMapObject
                val fps = 1000f / animatedTile.animationIntervals[0]
                entity[Animation].speed = fps * Animation.defaultSpeed
            }
        }
    }
}

operator fun MapObject.component1(): String =
    this.name ?: gdxError("MapObject ${this.id} at (${this.x}, ${this.y}) does not have a name")

operator fun MapObject.component2(): Float = this.x * MysticGarden.unitScale

operator fun MapObject.component3(): Float = this.y * MysticGarden.unitScale

operator fun MapObject.component4(): Int = this.id