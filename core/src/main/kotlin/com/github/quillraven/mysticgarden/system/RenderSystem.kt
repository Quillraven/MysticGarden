package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
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

    init {
        eventDispatcher.register<MapChangeEvent> { mapRenderer.map = it.map }
    }

    override fun onTick() {
        gameViewport.apply()

        if (mapRenderer.map != null) {
            mapRenderer.setView(gameCamera)
            mapRenderer.render()
        }

        batch.use(gameCamera) {
            super.onTick()
        }
    }

    override fun onTickEntity(entity: Entity) {
        val (sprite) = entity[Render]
        val (x, y, w, h) = entity[Boundary]
        val (_, _, spriteW, spriteH) = sprite

        sprite.setBounds(x, y, w, h)
        if (w != spriteW || h != spriteH) {
            // setOriginCenter is setting the dirty flag inside a Sprite.
            // For performance reasons we only want to do that when really necessary.
            sprite.setOriginCenter()
        }
        sprite.draw(batch)
    }

    override fun onDispose() {
        mapRenderer.disposeSafely()
    }
}
