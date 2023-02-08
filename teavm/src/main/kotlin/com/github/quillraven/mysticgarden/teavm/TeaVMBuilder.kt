package com.github.quillraven.mysticgarden.teavm

import java.io.File
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier
import com.github.xpenatan.gdx.backends.web.gen.SkipClass

/** Builds the TeaVM/HTML application. */
@SkipClass
object TeaVMBuilder {
    @JvmStatic fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(File("../assets"))
            webappPath = File("build/dist").canonicalPath
            // You can switch this setting during development:
            obfuscate = true
            // Register any extra classpath assets here:
            // additionalAssetsClasspathFiles += "com/github/quillraven/mysticgarden/asset.extension"
        }

        // Register any classes or packages that require reflection here:
        // TeaReflectionSupplier.addReflectionClass("com.github.quillraven.mysticgarden.reflect")

        val tool = TeaBuilder.config(teaBuildConfiguration)
        tool.mainClass = "com.github.quillraven.mysticgarden.teavm.TeaVMLauncher"
        TeaBuilder.build(tool)
    }
}
