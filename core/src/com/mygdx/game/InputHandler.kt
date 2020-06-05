package com.mygdx.game

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.MathUtils

class InputHandler(private val grid: Grid) : InputProcessor {

    private val leftTuning = InputTuning(grid, false)
    private val rightTuning = InputTuning(grid, true)

    private var sdTimer = 0f
    private var startSoftDrop = false

    fun update(dt: Float) {
        leftTuning.update(dt)
        rightTuning.update(dt)

        if (startSoftDrop) {
            if (SDS == 0f) grid.instantSoftDrop()
            else {
                sdTimer += dt
                if (sdTimer >= SDS) {
                    grid.currPiece.move(0, -1)
                    sdTimer = 0f
                }
            }
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.RIGHT -> {
                grid.rightHeld = false

                if (leftTuning.inProgress) leftTuning.start()
                rightTuning.end()
                if (!grid.leftHeld) grid.toggleLockDelay2(false)
            }
            Input.Keys.LEFT -> {
                grid.leftHeld = false

                if (rightTuning.inProgress) rightTuning.start()
                leftTuning.end()
                if (!grid.rightHeld) grid.toggleLockDelay2(false)
            }
            Input.Keys.DOWN -> {
                startSoftDrop = false
            }
        }
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.RIGHT -> {
                grid.rightHeld = true

                if (leftTuning.inProgress) leftTuning.reset()
                rightTuning.start()
                grid.currPiece.move(1, 0)
                if (grid.currPiece.canMove(1, 0)) grid.toggleLockDelay2(true)
            }
            Input.Keys.LEFT -> {
                grid.leftHeld = true

                if (rightTuning.inProgress) rightTuning.reset()
                leftTuning.start()
                grid.currPiece.move(-1, 0)
                if (grid.currPiece.canMove(-1, 0)) grid.toggleLockDelay2(true)
            }
            Input.Keys.DOWN -> {
                startSoftDrop = true
            }
            Input.Keys.SPACE -> {
                grid.hardDrop()
            }
            Input.Keys.UP -> {
                grid.onRotate()
                grid.currPiece.rotate(Rotation.Clockwise)
            }
            Input.Keys.Z -> {
                grid.onRotate()
                grid.currPiece.rotate(Rotation.Counterclockwise)
            }
            Input.Keys.X -> {
                grid.onRotate()
                grid.currPiece.rotate(Rotation.OneEighty)
            }
            Input.Keys.SHIFT_LEFT -> {
                grid.holdCurrPiece()
            }
            Input.Keys.R -> {
                grid.reset()
            }
            Input.Keys.G -> {
                grid.queueGarbage(MathUtils.random(1, 6))
            }
            Input.Keys.S -> {
                grid.addSolidGarbage(10)
            }
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = true

    override fun keyTyped(character: Char): Boolean = true

    override fun scrolled(amount: Int): Boolean = true

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = true

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true
}