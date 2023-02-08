package com.github.quillraven.mysticgarden.component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.AtlasAsset
import com.github.quillraven.mysticgarden.RegionName

typealias RegionAnimation = com.badlogic.gdx.graphics.g2d.Animation<TextureRegion>

data class Animation(
    val animation: RegionAnimation,
    var stateTime: Float = 0f,
    var speed: Float = 1f,
    var mode: PlayMode = PlayMode.LOOP
) : Component<Animation> {

    override fun type(): ComponentType<Animation> = Animation

    companion object : ComponentType<Animation>() {
        private const val defaultSpeed = 1 / 20f // 20 fps

        fun of(assets: Assets, region: RegionName): Animation {
            val regions = assets.getRegions(AtlasAsset.GAME, region)
            return Animation(
                RegionAnimation(defaultSpeed, regions)
            )
        }
    }

}