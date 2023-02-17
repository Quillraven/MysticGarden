package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Json
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.PhysicWorld
import com.github.quillraven.mysticgarden.component.Animation
import com.github.quillraven.mysticgarden.component.Player
import com.github.quillraven.mysticgarden.component.Remove
import com.github.quillraven.mysticgarden.component.Tiled
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.Trigger
import com.github.quillraven.mysticgarden.event.ZoneChangeEvent
import com.github.quillraven.mysticgarden.spawnObject
import com.github.quillraven.mysticgarden.spawnPlayer
import ktx.app.gdxError
import ktx.box2d.body
import ktx.box2d.box
import ktx.collections.GdxArray
import ktx.log.Logger
import ktx.math.vec2
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.tiled.*

class MapSystem(
    eventDispatcher: EventDispatcher = inject(),
    private val physicWorld: PhysicWorld = inject(),
    private val prefs: Preferences = inject(),
) : IntervalSystem() {

    private val collisionBodies = GdxArray<Body>()
    private val tiledEntities = world.family { all(Tiled) }
    private val playerEntities = world.family { all(Player) }

    init {
        eventDispatcher.register(this::onZoneChange)
    }

    override fun onTick() {
        AnimatedTiledMapTile.updateAnimationBaseTime()
    }

    private fun onZoneChange(event: ZoneChangeEvent) {
        val (_, map, newZone, oldZone) = event

        if (oldZone.isNotEmpty) {
            destroyAndSaveObjects(oldZone)
            destroyCollision()
        }

        if (playerEntities.isEmpty) {
            val (_, playerX, playerY) = map.startLocation
            val player = world.spawnPlayer(playerX, playerY)
            player[Player].maxCrystals = map.objectsLayers.objects.count { it.name == "Crystal" }
        }

        spawnObjects(map, newZone, oldZone)
        spawnCollision(map, newZone)
    }

    private fun destroyCollision() {
        log.debug { "Destroying ${collisionBodies.size} collision bodies" }
        collisionBodies.forEach { physicWorld.destroyBody(it) }
        collisionBodies.clear()
    }

    private fun destroyAndSaveObjects(zone: Zone) {
        log.debug { "Destroying ${tiledEntities.numEntities} tiled objects" }
        saveObjects(zone)
        tiledEntities.forEach { it.configure { e -> e += Remove(CameraSystem.maxPanTime) } }
    }

    private fun prefZoneObjectsKey(zone: Zone) = "zone-${zone.id}-objects"

    private fun saveObjects(zone: Zone) {
        prefs.flush {
            this[prefZoneObjectsKey(zone)] =
                Json().toJson(tiledEntities.entities.map { it[Tiled].id }, Array::class.java, Int::class.java)
        }
    }

    private val MapObject.animatedTile: AnimatedTiledMapTile?
        get() {
            if (this is TiledMapTileMapObject && this.tile is AnimatedTiledMapTile) {
                return this.tile as AnimatedTiledMapTile
            }
            return null
        }

    private fun spawnObjects(map: TiledMap, newZone: Zone, oldZone: Zone) {
        val newZonePrefObjs = Json().fromJson(IntArray::class.java, prefs[prefZoneObjectsKey(newZone), ""])
        val oldZonePrefObjs = Json().fromJson(IntArray::class.java, prefs[prefZoneObjectsKey(oldZone), ""])

        map.objectsLayers.objects
            .filter { (_, x, y, w, h, id) ->
                // only create objects that are part of the new zone
                val centerX = x + w * 0.5f
                val centerY = y + h * 0.5f

                newZone.contains(centerX, centerY)
                        // and which did not get removed before (e.g. cut tree via axe)
                        && (newZonePrefObjs == null || id in newZonePrefObjs)
                        // and which are not part of the previous zone or still exist in the previous zone
                        // -> this is to prevent objects from spawning that are part of two zones
                        //    and got already removed in one of the zones
                        && (!oldZone.contains(centerX, centerY) || oldZonePrefObjs?.contains(id) == true)
            }
            .forEach { mapObj ->
                val entity = world.spawnObject(mapObj)

                if (entity has Animation) {
                    // adjust the animation speed to the speed defined in Tiled map editor
                    val animatedTile = mapObj.animatedTile ?: return@forEach
                    val fps = 1000f / animatedTile.animationIntervals[0]
                    entity[Animation].speed = fps * Animation.defaultSpeed
                }
            }
    }

    private operator fun TiledMapTileLayer.component1(): Int = this.width

    private operator fun TiledMapTileLayer.component2(): Int = this.height

    private fun TiledMap.forEachCell(zone: Zone, action: (x: Int, y: Int, cell: Cell) -> Unit) {
        val (zoneX, zoneY, zoneW, zoneH) = zone.rect

        this.forEachLayer<TiledMapTileLayer> { layer ->
            repeat(MathUtils.ceil(zoneW)) { x ->
                repeat(MathUtils.ceil(zoneH)) { y ->
                    val cellX = zoneX.toInt() + x
                    val cellY = zoneY.toInt() + y
                    layer.getCell(cellX, cellY)?.let { action(cellX, cellY, it) }
                }
            }
        }
    }

    private fun spawnCollision(map: TiledMap, zone: Zone) {
        map.forEachCell(zone) { x, y, cell ->
            if (cell.tile.objects.isEmpty()) {
                return@forEachCell
            }

            collisionBodies.add(
                physicWorld.body {
                    position.set(x.toFloat(), y.toFloat())

                    val isWater = cell.tile.property("water", false)
                    cell.tile.objects.forEach { cellObject ->
                        if (cellObject !is RectangleMapObject) {
                            gdxError("Unsupported cell object $cellObject")
                        }

                        val (objX, objY, objW, objH) = cellObject.rectangle.scl(MysticGarden.unitScale)
                        box(objW, objH, vec2(objX + objW * 0.5f, objY + objH * 0.5f)) {
                            filter.categoryBits = if (isWater) MysticGarden.b2dWater else MysticGarden.b2dEnvironment
                        }
                    }
                }
            )
        }
    }

    companion object {
        private val log = Logger(MapSystem::class.java.simpleName)
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

operator fun MapObject.component4(): Float = this.width * MysticGarden.unitScale

operator fun MapObject.component5(): Float = this.height * MysticGarden.unitScale

operator fun MapObject.component6(): Int = this.id

val MapObject.trigger: Trigger?
    get() {
        val triggerName = this.propertyOrNull<String>("Trigger") ?: return null
        return Trigger.valueOf(triggerName)
    }

val TiledMap.zoneLayer: MapLayer
    get() = this.layer("zones")

val TiledMap.objectsLayers: MapLayer
    get() = this.layer("objects")

val TiledMap.startLocation: MapObject
    get() {
        return this.zoneLayer.objects
            .firstOrNull { it.name == "START_LOCATION" }
            ?: gdxError("There is no START_LOCATION defined in the zones layer")
    }
