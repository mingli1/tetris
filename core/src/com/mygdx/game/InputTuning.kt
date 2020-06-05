package com.mygdx.game

const val DAS = 0.117f
const val ARR = 0f
const val SDS = 0f

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