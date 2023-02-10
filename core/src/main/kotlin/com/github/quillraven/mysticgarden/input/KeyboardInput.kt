package com.github.quillraven.mysticgarden.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.app.KtxInputAdapter
import ktx.math.vec2

class KeyboardInput(private val playerController: PlayerController) : KtxInputAdapter {

    private val moveVec = vec2()

    init {
        Gdx.input.inputProcessor = this
    }

    private fun directionChange(x: Float, y: Float) {
        moveVec.add(x, y)
        playerController.move(moveVec.x, moveVec.y)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> directionChange(0f, 1f)
            Input.Keys.S -> directionChange(0f, -1f)
            Input.Keys.A -> directionChange(-1f, 0f)
            Input.Keys.D -> directionChange(1f, 0f)
            else -> return false
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> directionChange(0f, -1f)
            Input.Keys.S -> directionChange(0f, 1f)
            Input.Keys.A -> directionChange(1f, 0f)
            Input.Keys.D -> directionChange(-1f, 0f)
            else -> return false
        }

        return true
    }


}