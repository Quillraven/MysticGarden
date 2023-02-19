package com.github.quillraven.mysticgarden.input

import com.badlogic.gdx.Input
import com.github.quillraven.mysticgarden.RegionName
import ktx.app.KtxInputAdapter
import ktx.math.vec2

class KeyboardInput(private val playerController: PlayerController) : KtxInputAdapter {

    private val moveVec = vec2()

    private fun directionChange(x: Float, y: Float) {
        moveVec.add(x, y)
        updateMove(moveVec.x, moveVec.y)
    }

    fun updateMove(x: Float, y: Float) {
        playerController.move(x, y)

        when {
            x > 0 -> playerController.changeAnimation(RegionName.HERO_RIGHT)
            x < 0 -> playerController.changeAnimation(RegionName.HERO_LEFT)
            y > 0 -> playerController.changeAnimation(RegionName.HERO_UP)
            y < 0 -> playerController.changeAnimation(RegionName.HERO_DOWN)
        }
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