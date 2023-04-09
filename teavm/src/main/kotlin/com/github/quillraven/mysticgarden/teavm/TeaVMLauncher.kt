@file:JvmName("TeaVMLauncher")

package com.github.quillraven.mysticgarden.teavm

import com.github.quillraven.mysticgarden.MysticGarden
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 0
        height = 0
    }

    // for now set mobile to true for mobile browsers
    // until we figure out how to distinguish between mobile and desktop browser
    MysticGarden.isMobile = true
    TeaApplication(MysticGarden(), config)
}
