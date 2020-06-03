package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Batch

private const val SQUARE_SIZE = 32
private const val BAG_SIZE = 7
private const val NUM_PREVIEWS = 5
private const val GRAVITY = 0.3
// lock delay from soft drop
private const val LOCK_DELAY_1 = 0.5f
// lock delay from soft drop and left or right movement (reset from rotation)
private const val LOCK_DELAY_2 = 5f
// max amount of time piece locks no matter
private const val LOCK_DELAY_3 = 20f

class Grid(
    private val width: Int,
    private val height: Int,
    private val screenX: Float,
    private val screenY: Float,
    private val res: Resources
) {

    private val PIECES_POOL = mutableListOf(
        Piece(this, PieceType.L),
        Piece(this, PieceType.J),
        Piece(this, PieceType.S),
        Piece(this, PieceType.Z),
        Piece(this, PieceType.T),
        Piece(this, PieceType.I),
        Piece(this, PieceType.O)
    )
    private val HOLD_POOL = listOf(
        Piece(this, PieceType.L),
        Piece(this, PieceType.J),
        Piece(this, PieceType.S),
        Piece(this, PieceType.Z),
        Piece(this, PieceType.T),
        Piece(this, PieceType.I),
        Piece(this, PieceType.O)
    )
    lateinit var currPiece: Piece
    var canHold = true
    private var holdPiece: Piece? = null
    private val bag = mutableListOf<Piece>()

    private val content = Array(height) { y ->
        Array(width) { x ->
            Unit(Square(this, PieceType.None), x, y, false)
        }
    }

    private var gravityTimer = 0f

    private var lockDelay1Timer = 0f
    private var lockDelay2Timer = 0f
    private var startLockDelay2 = false
    private var lockDelay3Timer = 0f

    private var startRotationTimer = false
    private var rotationTimer = 0f
    private var longRotationTimer = 0f

    init { reset() }

    fun update(dt: Float) {
        gravityTimer += dt
        if (gravityTimer >= GRAVITY) {
            currPiece.move(0, -1)
            gravityTimer = 0f
        }

        if (!currPiece.canMove(0, -1) && !startLockDelay2) {
            if (startRotationTimer) {
                longRotationTimer += dt
                rotationTimer += dt
                if (rotationTimer >= LOCK_DELAY_1) {
                    hardDrop()
                    startRotationTimer = false
                }
                if (longRotationTimer >= LOCK_DELAY_2) {
                    hardDrop()
                    startRotationTimer = false
                }
            } else {
                lockDelay1Timer += dt
                if (lockDelay1Timer >= LOCK_DELAY_1) hardDrop()
            }
        } else {
            lockDelay1Timer = 0f
        }

        if (startLockDelay2) {
            lockDelay2Timer += dt
            if (lockDelay2Timer >= LOCK_DELAY_2) {
                startLockDelay2 = false
                hardDrop()
            }
        }

        lockDelay3Timer += dt
        if (lockDelay3Timer >= LOCK_DELAY_3) hardDrop()
    }

    fun isWithinBounds(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height * 2
    }

    fun isUnitEmpty(x: Int, y: Int): Boolean {
        if (y >= height) return true
        return !content[y][x].filled
    }

    fun addSquare(x: Int, y: Int, square: Square) {
        content[y][x].square.let {
            it.x = square.x
            it.y = square.y
            it.pieceType = square.pieceType
        }
        content[y][x].filled = true
    }

    fun isWithinHeight(y: Int) = y < height

    fun hardDrop() {
        instantSoftDrop()
        currPiece.lock()
        currPiece = getNextPiece()
    }

    fun instantDas(right: Boolean) {
        for (i in 0 until width) {
            if (!currPiece.move(if (right) 1 else -1, 0)) break
        }
    }

    fun instantSoftDrop() {
        for (i in 0 until height + 1) {
            if (!currPiece.move(0, -1)) break
        }
    }

    fun holdCurrPiece() {
        if (canHold) {
            val piece = holdPiece
            holdPiece = HOLD_POOL.find { it.pieceType == currPiece.pieceType }?.apply { init(0, 0) }
            currPiece = piece?.pieceType?.let { hold ->
                PIECES_POOL.find { it.pieceType == hold }?.apply { init(4, height + 1) }
            } ?: getNextPiece()
            canHold = false
        }
    }

    fun attemptLineClears() {
        var startRow = -1
        var numLinesToClear = 0
        for (y in height - 1 downTo 0) {
            if (content[y].all { it.filled }) {
                if (startRow == -1) startRow = y
                numLinesToClear++
            }
        }
        if (numLinesToClear == 0) return
        for (i in startRow + 1 until height) {
            content[i - numLinesToClear].forEachIndexed { index, unit ->
                val topUnit = content[i][index]
                unit.filled = topUnit.filled
                unit.square.pieceType = topUnit.square.pieceType

                topUnit.filled = false
                topUnit.square.pieceType = PieceType.None
            }
        }
    }

    fun reset() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                content[y][x].let {
                    it.filled = false
                    it.square.pieceType = PieceType.None
                }
            }
        }
        bag.clear()
        repeat(2) { addToBag() }
        currPiece = getNextPiece()
        holdPiece = null
        canHold = true
    }

    fun toggleLockDelay2(start: Boolean) {
        startLockDelay2 = start
    }

    fun onRotate() {
        startRotationTimer = true
        rotationTimer = 0f
        lockDelay2Timer = 0f
    }

    fun render(batch: Batch) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                batch.draw(res.getTexture("unit"),
                    screenX + content[y][x].x * SQUARE_SIZE,
                    screenY + content[y][x].y * SQUARE_SIZE)

                if (content[y][x].filled) {
                    batch.draw(res.getSquare(content[y][x].square.pieceType),
                        screenX + content[y][x].x * SQUARE_SIZE,
                        screenY + content[y][x].y * SQUARE_SIZE)
                }
            }
        }
        currPiece.squares.forEach {
            batch.draw(res.getSquare(currPiece.pieceType),
                screenX + it.x * SQUARE_SIZE,
                screenY + it.y * SQUARE_SIZE)
            batch.draw(res.getGhost(currPiece.pieceType),
                screenX + it.x * SQUARE_SIZE,
                screenY + getGhostPieceY(it) * SQUARE_SIZE)
        }
        holdPiece?.let { piece ->
            piece.squares.forEach {
                batch.draw(res.getSquare(piece.pieceType),
                    (screenX - 96) + (it.x * SQUARE_SIZE),
                    (screenY + ((height - 4) * SQUARE_SIZE)) + (it.y * SQUARE_SIZE))
            }
        }
        for (i in 0 until NUM_PREVIEWS) {
            val piece = bag[i]
            val x = screenX + (width * SQUARE_SIZE) + 64
            val y = screenY + ((height - 4) * SQUARE_SIZE) - (i * 108)
            piece.previewSquares.forEach {
                batch.draw(res.getSquare(piece.pieceType),
                    x + it.x * SQUARE_SIZE,
                    y + it.y * SQUARE_SIZE)
            }
        }
    }

    private fun getGhostPieceY(square: Square): Int {
        var y = square.y
        var offset = -1
        for (i in 0 until height + 1) {
            if (!currPiece.canMove(0, offset)) break
            y--
            offset--
        }
        return y
    }

    private fun getNextPiece(): Piece {
        val nextPiece = bag.removeAt(0).apply {
            init(4, height + 1)
        }
        if (bag.size <= BAG_SIZE) addToBag()

        resetTimers()
        return nextPiece
    }

    private fun addToBag() {
        PIECES_POOL.shuffle()
        bag.addAll(PIECES_POOL)
    }

    private fun resetTimers() {
        lockDelay1Timer = 0f
        lockDelay2Timer = 0f
        lockDelay3Timer = 0f

        rotationTimer = 0f
        longRotationTimer = 0f
    }
}