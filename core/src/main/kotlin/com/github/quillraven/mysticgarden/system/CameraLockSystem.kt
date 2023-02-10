package com.github.quillraven.mysticgarden.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.mysticgarden.component.Boundary
import com.github.quillraven.mysticgarden.component.CameraLock

class CameraLockSystem(
    private val gameCamera: OrthographicCamera = inject(),
) : IteratingSystem(family { all(CameraLock, Boundary) }) {

    override fun onTickEntity(entity: Entity) {
        val (x, y, w, h) = entity[Boundary]
        gameCamera.position.set(x + w * 0.5f, y + h * 0.5f, gameCamera.position.z)
    }
}