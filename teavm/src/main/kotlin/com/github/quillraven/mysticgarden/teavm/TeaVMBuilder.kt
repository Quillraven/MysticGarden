package com.github.quillraven.mysticgarden.teavm

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass
import java.io.File

/** Builds the TeaVM/HTML application. */
@SkipClass
object TeaVMBuilder {
    @JvmStatic
    fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(File("../assets"))
            webappPath = File("build/dist").canonicalPath

            htmlTitle = "Mystic Garden"
            htmlWidth = 432
            htmlHeight = 768
        }

        val tool = TeaBuilder.config(teaBuildConfiguration)
        tool.mainClass = "com.github.quillraven.mysticgarden.teavm.TeaVMLauncher"
        TeaBuilder.build(tool)
    }
}