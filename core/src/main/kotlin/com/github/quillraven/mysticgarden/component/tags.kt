package com.github.quillraven.mysticgarden.component

import com.github.quillraven.fleks.EntityTags
import com.github.quillraven.fleks.entityTagOf

enum class Tags : EntityTags by entityTagOf() {
    CAMERA_LOCK,
}