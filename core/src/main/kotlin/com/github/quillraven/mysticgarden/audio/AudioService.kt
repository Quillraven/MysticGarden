package com.github.quillraven.mysticgarden.audio

import com.badlogic.gdx.audio.Music
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.MusicAsset
import com.github.quillraven.mysticgarden.SoundAsset
import ktx.collections.GdxArray

class AudioService(private val assets: Assets) {

    private val sndQueue = GdxArray<SoundAsset>()
    private var music: Music? = null
    var sndVolume = 1f
    var mscVolume = 1f
        set(value) {
            music?.volume = value
            field = value
        }

    fun play(type: SoundAsset) {
        if (type !in sndQueue) {
            sndQueue.add(type)
        }
    }

    fun play(type: MusicAsset) {
        music?.stop()
        music = assets[type].also {
            it.isLooping = true
            it.volume = mscVolume
            it.play()
        }
    }

    fun update() {
        if (sndQueue.isEmpty) {
            return
        }

        sndQueue.forEach { sndType ->
            assets[sndType].play(sndVolume)
        }
        sndQueue.clear()
    }
}