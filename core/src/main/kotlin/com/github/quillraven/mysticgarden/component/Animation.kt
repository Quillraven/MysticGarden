package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.RegionName

typealias RegionAnimation = com.badlogic.gdx.graphics.g2d.Animation<TextureRegion>

data class Animation(
    var animation: RegionAnimation,
    var speed: Float,
    var stateTime: Float = 0f,
    var mode: PlayMode = PlayMode.LOOP
) : Component<Animation> {

    val firstFrame: TextureRegion
        get() = animation.getKeyFrame(0f)

    fun change(assets: Assets, region: RegionName) {
        animation = RegionAnimation(defaultSpeed, assets.getRegions(region))
    }

    override fun type(): ComponentType<Animation> = Animation

    companion object : ComponentType<Animation>() {
        const val defaultSpeed = 1 / 20f // 20 fps

        fun of(assets: Assets, region: RegionName, speed: Float = 1f): Animation {
            val regions = assets.getRegions(region)
            return Animation(RegionAnimation(defaultSpeed, regions), speed)
        }
    }

}