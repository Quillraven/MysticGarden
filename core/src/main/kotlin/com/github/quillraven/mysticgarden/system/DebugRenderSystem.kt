package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.*
import ktx.assets.disposeSafely
import ktx.graphics.use

class DebugRenderSystem(
    private val gameCamera: OrthographicCamera = inject()
) : IteratingSystem(family { all(Boundary, Render) }) {

    private val profiler = GLProfiler(Gdx.graphics).apply { enable() }
    private val shapeRenderer = ShapeRenderer()

    override fun onTick() {
        Gdx.graphics.setTitle("FPS: ${Gdx.graphics.framesPerSecond} - Draw calls: ${profiler.drawCalls} - Bindings: ${profiler.textureBindings}")

        shapeRenderer.use(ShapeRenderer.ShapeType.Line, gameCamera) {
            super.onTick()
        }

        profiler.reset()
    }

    override fun onTickEntity(entity: Entity) {
        val (x, y, w, h) = entity[Boundary]
        val boundaryColor = Color.TEAL
        shapeRenderer.rect(x, y, w, h, boundaryColor, boundaryColor, boundaryColor, boundaryColor)

        val spriteColor = Color.WHITE
        val (spriteX, spriteY, spriteW, spriteH) = entity[Render].sprite
        shapeRenderer.rect(spriteX, spriteY, spriteW, spriteH, spriteColor, spriteColor, spriteColor, spriteColor)
    }

    override fun onDispose() {
        shapeRenderer.disposeSafely()
    }
}