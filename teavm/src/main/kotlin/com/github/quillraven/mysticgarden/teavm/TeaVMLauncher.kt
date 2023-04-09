@file:JvmName("TeaVMLauncher")

package com.github.quillraven.mysticgarden.teavm

import com.github.quillraven.mysticgarden.MysticGarden
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.google.gwt.user.client.Window

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 0
        height = 0
    }

    val userAgent = Window.Navigator.getUserAgent() ?: ""
    MysticGarden.isMobile = userAgent.contains("Mobile") || userAgent.contains("Android")

    TeaApplication(MysticGarden(), config)
}
