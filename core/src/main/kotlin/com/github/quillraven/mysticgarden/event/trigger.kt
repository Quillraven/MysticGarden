package com.github.quillraven.mysticgarden.event

import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.component.Tiled
import com.github.quillraven.mysticgarden.component.TiledObjectType

enum class Trigger(val action: (World) -> Unit) {
    KILL_TUTORIAL_TREES(World::killTutorialTrees)
}

private fun World.killTutorialTrees() {
    this.family { all(Tiled) }.entities
        .filter { it[Tiled].type == TiledObjectType.TUTORIAL_TREE }
        .forEach { it.remove() }
}