@file:JvmName("TeaVMLauncher")

package com.github.quillraven.mysticgarden.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.web.WebApplication
import com.github.quillraven.mysticgarden.MysticGarden

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 640
        height = 480
    }
    WebApplication(MysticGarden(), config)
}
