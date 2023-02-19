package com.github.quillraven.mysticgarden.ui.model

import com.github.quillraven.fleks.World
import com.github.quillraven.mysticgarden.input.KeyboardInput

class GameModel(
    private val world: World,
    private val keyboardInput: KeyboardInput,
) {

    fun onTouchChange(x: Float, y: Float) {
        keyboardInput.updateMove(x, y)
    }
}