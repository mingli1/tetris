package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils

private const val SQUARE_SIZE = 32
private const val BAG_SIZE = 7
private const val NUM_PREVIEWS = 5
private const val GRAVITY = 1
// lock delay from soft drop
private const val LOCK_DELAY_1 = 0.5f
// lock delay from soft drop and left or right movement (reset from rotation)
private const val LOCK_DELAY_2 = 5f
// max amount of time piece locks no matter
private const val LOCK_DELAY_3 = 20f
// time after piece placed that garbage appears
private const val GARBAGE_DELAY = 0.5f

class Grid(
    private val width: Int,
    val height: Int,
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
    private val garbage = mutableListOf<Int>()

    private val content = Array(height * 2) { y ->
        Array(width) { x ->
            Unit(Square(this, PieceType.None), x, y, false)
        }
    }
    private val rowsToClear = mutableListOf<Int>()
    var piecesPlaced = 0
    private var combo = 0
    private var b2b = 0
    private var linesSent = 0
    private var totalAttack = 0
    private var attack = 0

    private var clockTimer = 0f
    private var gravityTimer = 0f

    private var lockDelay1Timer = 0f
    private var lockDelay2Timer = 0f
    private var startLockDelay2 = false
    private var lockDelay3Timer = 0f

    private var startRotationTimer = false
    private var rotationTimer = 0f
    private var longRotationTimer = 0f
    private var startGarbageTimer = false
    private var garbageTimer = 0f

    var rightHeld = false
    var leftHeld = false
    private var solidGarbageRow = 0

    val stats = Array(15) { 0f }

    init { reset() }

    fun update(dt: Float) {
        clockTimer += dt
        stats[TIME] = clockTimer
        stats[PPS] = piecesPlaced / clockTimer
        stats[COMBO] = combo.toFloat()
        stats[B2B] = b2b.toFloat()
        stats[ATT] = totalAttack.toFloat()
        stats[APM] = totalAttack / clockTimer * 60
        stats[LINES_SENT] = linesSent.toFloat()

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
            if ((leftHeld && !currPiece.canMove(-1, 0)) || (rightHeld && !currPiece.canMove(1, 0))) {
                startLockDelay2 = false
            } else {
                lockDelay2Timer += dt
                if (lockDelay2Timer >= LOCK_DELAY_2) {
                    startLockDelay2 = false
                    hardDrop()
                }
            }
        }

        lockDelay3Timer += dt
        if (lockDelay3Timer >= LOCK_DELAY_3) hardDrop()

        if (startGarbageTimer) {
            garbageTimer += dt
            if (garbageTimer >= GARBAGE_DELAY) {
                receiveGarbage()
                garbageTimer = 0f
                startGarbageTimer = false
            }
        }
    }

    fun isWithinBounds(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height * 2
    }

    fun isUnitEmpty(x: Int, y: Int): Boolean {
        return !content[y][x].filled
    }

    fun queueGarbage(numLines: Int) {
        garbage.add(numLines)
    }

    fun addSquare(x: Int, y: Int, square: Square) {
        content[y][x].square.let {
            it.x = square.x
            it.y = square.y
            it.pieceType = square.pieceType
        }
        content[y][x].filled = true
    }

    fun addSolidGarbage(numLines: Int) {
        offsetStack(numLines)
        solidGarbageRow = numLines

        for (y in 0 until numLines) {
            for (x in 0 until width) {
                content[y][x].run {
                    filled = true
                    square.pieceType = PieceType.Solid
                }
            }
        }
        // top out
        if (numLines >= height) reset()
        if (!currPiece.canMove(0, -1)) reset()
    }

    fun isWithinHeight(y: Int) = y < height * 2

    fun hardDrop() {
        instantSoftDrop()
        currPiece.lock()
        if (currPiece.isToppedOut()) reset()
        else currPiece = getNextPiece()
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

    fun getLineClears(squares: Array<Square>) {
        rowsToClear.clear()
        attack = 0

        for (y in height - 1 downTo 0) {
            var rowFilled = true
            for (x in 0 until width) {
                if (content[y][x].square.pieceType == PieceType.Solid || !(content[y][x].filled || squares.any { it.x == x && it.y == y })) {
                    rowFilled = false
                }
            }
            if (rowFilled) rowsToClear.add(y)
        }
        val numLinesToClear = rowsToClear.size

        if (numLinesToClear == 0) {
            combo = 0
            if (garbage.isNotEmpty()) {
                startGarbageTimer = true
            }
            return
        } else {
            combo++
        }

        attack += when (combo) {
            1 -> 0
            2, 3, 4 -> 1
            5, 6 -> 2
            7, 8 -> 3
            9, 10, 11 -> 4
            else -> 5
        }

        val applyB2bBonus = b2b > 0

        if (currPiece.pieceType == PieceType.T) {
            // t spin
            if (!currPiece.canMove(-1, 0) && !currPiece.canMove(1, 0) && !currPiece.canMove(0, 1)) {
                applyTSpin(numLinesToClear, applyB2bBonus)
                b2b++
                return
            }
        }
        applyLineClears(numLinesToClear, applyB2bBonus)

        if (numLinesToClear < 4) b2b = 0
        else b2b++
    }

    fun clearLines() {
        if (rowsToClear.isEmpty()) return
        rowsToClear.forEach { row ->
            if (row + 1 < height * 2) {
                for (i in row + 1 until height * 2) {
                    content[i - 1].forEachIndexed { index, unit ->
                        val topUnit = content[i][index]
                        unit.filled = topUnit.filled
                        unit.square.pieceType = topUnit.square.pieceType

                        topUnit.filled = false
                        topUnit.square.pieceType = PieceType.None
                    }
                }
            }
        }

        if (content.all { row -> row.all { !it.filled } }) {
            stats[PC]++
            attack += 10
        }
        totalAttack += attack
        cancelGarbage()
        linesSent += attack
    }

    private fun applyLineClears(lines: Int, b2b: Boolean) {
        when (lines) {
            1 -> stats[SINGLE]++
            2 -> {
                stats[DOUBLE]++
                attack += 1
            }
            3 -> {
                stats[TRIPLE]++
                attack += 2
            }
            4 -> {
                stats[QUAD]++
                attack += if (b2b) 5 else 4
            }
        }
    }

    private fun applyTSpin(lines: Int, b2b: Boolean) {
        when (lines) {
            1 -> {
                stats[TSS]++
                attack += if (b2b) 3 else 2
            }
            2 -> {
                stats[TSD]++
                attack += if (b2b) 5 else 4
            }
            3 -> {
                stats[TST]++
                attack += if (b2b) 7 else 6
            }
        }
    }

    fun reset() {
        for (y in 0 until height * 2) {
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
        piecesPlaced = 0
        clockTimer = 0f
        garbage.clear()

        combo = 0
        b2b = 0
        totalAttack = 0
        linesSent = 0

        solidGarbageRow = 0
        startLockDelay2 = false
        startGarbageTimer = false
        garbageTimer = 0f

        for (i in stats.indices) stats[i] = 0f
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
        for (y in 0 until height * 2) {
            for (x in 0 until width) {
                if (y < height) {
                    batch.draw(res.unit,
                        screenX + content[y][x].x * SQUARE_SIZE,
                        screenY + content[y][x].y * SQUARE_SIZE)
                }

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
                    (screenX - 128) + (it.x * SQUARE_SIZE),
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
        batch.draw(res.red, screenX - 10, screenY, 10f, getGarbageBarHeight())
    }

    private fun receiveGarbage() {
        val lines = garbage.sum()
        offsetStack(lines)
        var currY = solidGarbageRow
        for (i in garbage.size - 1 downTo 0) {
            var numLines = garbage[i]
            if (currY + numLines > height * 2) {
                numLines = height * 2 - currY
            }
            val holeX = MathUtils.random(width - 1)
            for (y in currY until currY + numLines) {
                for (x in 0 until width) {
                    if (x != holeX) {
                        content[y][x].run {
                            filled = true
                            square.pieceType = PieceType.Garbage
                        }
                    }
                }
            }
            currY += numLines
            if (currY >= height * 2) break
        }
        // top out
        if (lines >= height) reset()
        if (!currPiece.canMove(0, -1)) reset()

        garbage.clear()
    }

    private fun cancelGarbage() {
        if (garbage.isEmpty()) return
        if (attack <= 0) return
        val totalGarbage = garbage.sum()
        var newAttack = attack - totalGarbage
        if (newAttack < 0) newAttack = 0

        val remainingGarbage = totalGarbage - attack
        attack = newAttack
        if (remainingGarbage <= 0) {
            garbage.clear()
            return
        }

        var numToDrop = 0
        var total = totalGarbage
        for (i in garbage.indices) {
            if (total - garbage[i] > remainingGarbage) {
                total -= garbage[i]
                numToDrop++
            } else if (total - garbage[i] < remainingGarbage) {
                garbage[i] -= total - remainingGarbage
                break
            } else {
                numToDrop++
                break
            }
        }
        repeat(numToDrop) {
            garbage.removeAt(0)
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

    private fun offsetStack(lines: Int) {
        var topOfStack = 0
        for (y in height - 1 downTo solidGarbageRow) {
            if (content[y].any { it.filled }) {
                topOfStack = y
                break
            }
        }

        for (y in topOfStack downTo solidGarbageRow) {
            for (x in 0 until width) {
                if (y + lines < height * 2) {
                    content[y + lines][x].run {
                        filled = content[y][x].filled
                        square.pieceType = content[y][x].square.pieceType
                    }
                }
                content[y][x].run {
                    filled = false
                    square.pieceType = PieceType.None
                }
            }
        }
    }

    private fun addToBag() {
        PIECES_POOL.shuffle()
        bag.addAll(PIECES_POOL)
    }

    private fun resetTimers() {
        gravityTimer = 0f

        lockDelay1Timer = 0f
        lockDelay2Timer = 0f
        lockDelay3Timer = 0f

        rotationTimer = 0f
        longRotationTimer = 0f
    }

    private fun getGarbageBarHeight(): Float {
        val lines = garbage.sum()
        val totalHeight = height.toFloat() * SQUARE_SIZE
        if (lines > height) return totalHeight
        return (lines / height.toFloat()) * totalHeight
    }
}