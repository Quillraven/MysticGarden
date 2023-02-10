package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class CameraLock : Component<CameraLock> {
    override fun type(): ComponentType<CameraLock> = CameraLock

    companion object : ComponentType<CameraLock>()
}