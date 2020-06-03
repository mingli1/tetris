package com.mygdx.game

class Piece(
    private val grid: Grid,
    val pieceType: PieceType
) {

    private var rotationIndex = 0
    val squares = Array(4) { Square(grid, pieceType) }
    val previewSquares = Array(4) { Square(grid, pieceType) }

    init {
        initSquares(previewSquares, 0, 0)
    }

    fun init(spawnX: Int, spawnY: Int) {
        rotationIndex = 0
        initSquares(squares, spawnX, spawnY)
    }

    fun lock() {
        squares.forEach { it.lock() }
        grid.canHold = true
        grid.attemptLineClears()
    }

    fun move(x: Int, y: Int): Boolean {
        if (!canMove(x, y)) return false
        squares.forEach { it.move(x, y) }
        return true
    }

    fun canMove(x: Int, y: Int) = squares.all { it.canMoveTo(it.x + x, it.y + y) }

    fun rotate(clockwise: Boolean, performOffsetTests: Boolean = true) {
        if (pieceType == PieceType.O) return

        val prevRotationIndex = rotationIndex
        rotationIndex += if (clockwise) 1 else -1
        rotationIndex = mod(rotationIndex, 4)

        squares.forEach { it.rotate(squares[0].x, squares[0].y, clockwise) }
        if (!performOffsetTests) return

        val offsetResult = performOffsetTests(prevRotationIndex, rotationIndex)
        if (!offsetResult) rotate(!clockwise, false)
    }

    private fun initSquares(squares: Array<Square>, spawnX: Int, spawnY: Int) {
        squares[0].updatePosition(spawnX, spawnY)

        when (pieceType) {
            PieceType.T -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.I -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX + 2, spawnY)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.L -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX + 1, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.J -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX - 1, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.S -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX + 1, spawnY + 1)
                squares[3].updatePosition(spawnX, spawnY + 1)
            }
            PieceType.Z -> {
                squares[1].updatePosition(spawnX, spawnY + 1)
                squares[2].updatePosition(spawnX - 1, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.O -> {
                squares[1].updatePosition(spawnX + 1, spawnY)
                squares[2].updatePosition(spawnX + 1, spawnY + 1)
                squares[3].updatePosition(spawnX, spawnY + 1)
            }
        }
    }

    private fun performOffsetTests(prevRotationIndex: Int, newRotationIndex: Int): Boolean {
        val offsetData = when (pieceType) {
            PieceType.I -> I_OFFSET_DATA
            else -> TSZLJ_OFFSET_DATA
        }

        var movePossible = false
        var endX = 0
        var endY = 0

        for (i in 0 until 5) {
            val offset1 = offsetData[i][prevRotationIndex]
            val offset2 = offsetData[i][newRotationIndex]
            endX = offset1.x - offset2.x
            endY = offset1.y - offset2.y

            if (canMove(endX, endY)) {
                movePossible = true
                break
            }
        }

        if (movePossible) move(endX, endY)
        return movePossible
    }

    private fun mod(x: Int, m: Int) = (x % m + m) % m
}