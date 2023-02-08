package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.collection.compareEntityBy
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.Render
import ktx.graphics.use

class RenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject(),
    private val gameCamera: OrthographicCamera = inject(),
) : IteratingSystem(
    family = family { all(Render, Boundary) },
    comparator = compareEntityBy(Boundary)
) {

    override fun onTick() {
        gameViewport.apply()
        batch.use(gameCamera.combined) {
            super.onTick()
        }
    }

    override fun onTickEntity(entity: Entity) {
        val (sprite) = entity[Render]
        val (x, y, w, h) = entity[Boundary]
        val spriteW = sprite.width
        val spriteH = sprite.height

        sprite.setBounds(x, y, w, h)
        if (w != spriteW || h != spriteH) {
            // setOriginCenter is setting the dirty flag inside a Sprite.
            // For performance reasons we only want to do that when really necessary.
            sprite.setOriginCenter()
        }
        sprite.draw(batch)
    }
}
