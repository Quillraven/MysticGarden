package com.github.quillraven.mysticgarden.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.mysticgarden.component.Animation
import com.github.quillraven.mysticgarden.component.Disable
import com.github.quillraven.mysticgarden.component.Render

class AnimationSystem : IteratingSystem(family { all(Animation, Render).none(Disable) }) {

    override fun onTickEntity(entity: Entity) {
        val aniCmp = entity[Animation]
        val (animation, speed, stateTime, mode) = aniCmp

        animation.playMode = mode
        val frame = animation.getKeyFrame(stateTime)
        aniCmp.stateTime += deltaTime * speed

        entity[Render].sprite.setRegion(frame)
    }
}