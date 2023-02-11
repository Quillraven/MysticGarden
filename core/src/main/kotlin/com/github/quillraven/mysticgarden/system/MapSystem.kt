package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.PhysicWorld
import com.github.quillraven.mysticgarden.component.Animation
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.event.Trigger
import com.github.quillraven.mysticgarden.spawnObject
import ktx.app.gdxError
import ktx.box2d.body
import ktx.box2d.box
import ktx.math.vec2
import ktx.tiled.*

class MapSystem(
    eventDispatcher: EventDispatcher = inject(),
    private val physicWorld: PhysicWorld = inject(),
) : IntervalSystem() {

    init {
        eventDispatcher.register(this::onMapChange)
    }

    override fun onTick() {
        AnimatedTiledMapTile.updateAnimationBaseTime()
    }

    private fun onMapChange(event: MapChangeEvent) {
        spawnObjects(event.map)
        spawnCollision(event.map)
    }

    private val MapObject.animatedTile: AnimatedTiledMapTile?
        get() {
            if (this is TiledMapTileMapObject && this.tile is AnimatedTiledMapTile) {
                return this.tile as AnimatedTiledMapTile
            }
            return null
        }

    private fun spawnObjects(map: TiledMap) {
        map.objectsLayers.objects.forEach { mapObj ->
            val entity = world.spawnObject(mapObj)
            if (entity has Animation) {
                // adjust the animation speed to the speed defined in Tiled mapeditor
                val animatedTile = mapObj.animatedTile ?: return@forEach
                val fps = 1000f / animatedTile.animationIntervals[0]
                entity[Animation].speed = fps * Animation.defaultSpeed
            }
        }
    }

    private operator fun TiledMapTileLayer.component1(): Int = this.width

    private operator fun TiledMapTileLayer.component2(): Int = this.height

    private fun TiledMap.forEachCell(action: (x: Int, y: Int, cell: Cell) -> Unit) {
        this.forEachLayer<TiledMapTileLayer> { layer ->
            val (w, h) = layer
            repeat(w) { x ->
                repeat(h) { y ->
                    layer.getCell(x, y)?.let { action(x, y, it) }
                }
            }
        }
    }

    private fun spawnCollision(map: TiledMap) {
        map.forEachCell { x, y, cell ->
            if (cell.tile.objects.isEmpty()) {
                return@forEachCell
            }

            physicWorld.body {
                position.set(x.toFloat(), y.toFloat())

                cell.tile.objects.forEach { cellObject ->
                    if (cellObject !is RectangleMapObject) {
                        gdxError("Unsupported cell object $cellObject")
                    }

                    val (objX, objY, objW, objH) = cellObject.rectangle.scl(MysticGarden.unitScale)
                    box(objW, objH, vec2(objX + objW * 0.5f, objY + objH * 0.5f))
                }
            }
        }
    }
}

operator fun Rectangle.component1(): Float = this.x

operator fun Rectangle.component2(): Float = this.y

operator fun Rectangle.component3(): Float = this.width

operator fun Rectangle.component4(): Float = this.height

fun Rectangle.scl(scale: Float): Rectangle = Rectangle.tmp.set(
    x * scale, y * scale, width * scale, height * scale
)

operator fun MapObject.component1(): String =
    this.name ?: gdxError("MapObject ${this.id} at (${this.x}, ${this.y}) does not have a name")

operator fun MapObject.component2(): Float = this.x * MysticGarden.unitScale

operator fun MapObject.component3(): Float = this.y * MysticGarden.unitScale

operator fun MapObject.component4(): Int = this.id

val MapObject.trigger: Trigger?
    get() {
        val triggerName = this.propertyOrNull<String>("Trigger") ?: return null
        return Trigger.valueOf(triggerName)
    }

val TiledMap.zoneLayer: MapLayer
    get() = this.layer("zones")

val TiledMap.objectsLayers: MapLayer
    get() = this.layer("objects")

val TiledMap.startLocation: Vector2
    get() {
        val (_, x, y) = this.objectsLayers.objects
            .firstOrNull { it.name == "START_LOCATION" }
            ?: gdxError("There is no START_LOCATION defined in the objects layer")
        return vec2(x, y)
    }
