@file:JvmName("TeaVMLauncher")

package com.github.quillraven.mysticgarden.teavm

import com.github.quillraven.mysticgarden.MysticGarden
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.web.WebApplication

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 432
        height = 768
    }
    WebApplication(MysticGarden(), config)
}
