package com.mygdx.game

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

private const val DAS = 0.117f
private const val ARR = 0f
private const val SDS = 0f

class InputTuning(
    private val grid: Grid,
    private val right: Boolean
) {

    var inProgress = false
    private var dasTimer = 0f
    private var arrTimer = 0f
    private var startDas = false
    private var startArr = false

    fun update(dt: Float) {
        if (startDas) {
            dasTimer += dt
            if (dasTimer >= DAS) {
                startArr = true
                startDas = false
            }
        }
        if (startArr) {
            arrTimer += dt
            if (ARR == 0f) {
                grid.instantDas(right)
            }
            else if (arrTimer >= ARR) {
                movePiece()
                arrTimer = 0f
            }
        }
    }

    fun start() {
        startDas = true
        inProgress = true
    }

    fun end() {
        reset()
        inProgress = false
    }

    fun reset() {
        startDas = false
        dasTimer = 0f
        startArr = false
        arrTimer = 0f
    }

    private fun movePiece() {
        when (right) {
            false -> grid.currPiece.move(-1, 0)
            true -> grid.currPiece.move(1, 0)
        }
    }
}

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
                grid.currPiece.rotate(true)
            }
            Input.Keys.Z -> {
                grid.onRotate()
                grid.currPiece.rotate(false)
            }
            Input.Keys.SHIFT_LEFT -> {
                grid.holdCurrPiece()
            }
            Input.Keys.R -> {
                grid.reset()
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