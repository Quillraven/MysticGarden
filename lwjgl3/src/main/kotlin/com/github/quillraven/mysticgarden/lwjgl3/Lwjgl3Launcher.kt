@file:JvmName("Lwjgl3Launcher")

package com.github.quillraven.mysticgarden.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.quillraven.mysticgarden.MysticGarden

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(MysticGarden(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("MysticGarden")
        setWindowedMode(432, 768)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
