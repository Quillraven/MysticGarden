package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.collection.compareEntityBy
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.component.*
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.collections.isNotEmpty
import ktx.graphics.use

class RenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject(),
    private val gameCamera: OrthographicCamera = inject(),
    eventDispatcher: EventDispatcher = inject(),
) : IteratingSystem(
    family = family { all(Render, Boundary) },
    comparator = compareEntityBy(Boundary)
) {

    private val mapRenderer = OrthogonalTiledMapRenderer(null, MysticGarden.unitScale, batch)
    private val tileLayers = GdxArray<TiledMapTileLayer>()

    init {
        eventDispatcher.register<MapChangeEvent> {
            mapRenderer.map = it.map
            mapRenderer.map.layers.getByType(TiledMapTileLayer::class.java, tileLayers)
        }
    }

    override fun onTick() {
        gameViewport.apply()

        batch.use(gameCamera) {
            if (tileLayers.isNotEmpty()) {
                // this game uses the map only as a background
                // -> so we can simply render everything before our entities gets rendered
                mapRenderer.setView(gameCamera)
                tileLayers.forEach(mapRenderer::renderTileLayer)
            }

            super.onTick()
        }
    }

    override fun onTickEntity(entity: Entity) {
        val (sprite) = entity[Render]
        val (x, y, w, _) = entity[Boundary]
        val (_, _, spriteW, _) = sprite

        // We do not mess with the size of the sprite to keep it simple.
        // All objects in the game have a proper sprite to fit the size of (1, 1).
        // The only exception is the player who has a bigger sprite and that's
        // why we center it on the x-axis.
        sprite.setPosition(x - (spriteW - w) * 0.5f, y)

        sprite.draw(batch)
    }

    override fun onDispose() {
        mapRenderer.disposeSafely()
    }
}
