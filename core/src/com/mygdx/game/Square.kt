package com.mygdx.game

class Square(
    private val grid: Grid,
    var pieceType: PieceType
) {

    var x = 0
    var y = 0

    fun move(x: Int, y: Int) = updatePosition(this.x + x, this.y + y)

    fun canMoveTo(x: Int, y: Int): Boolean {
        return grid.isWithinBounds(x, y) && grid.isUnitEmpty(x, y)
    }

    fun updatePosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun lock(): Boolean {
        if (!grid.isWithinHeight(y)) return false
        grid.addSquare(x, y, this)
        return true
    }

    fun rotate(originX: Int, originY: Int, clockwise: Boolean) {
        val relX = x - originX
        val relY = y - originY
        val data = if (clockwise) CW_ROTATE_DATA else CCW_ROTATE_DATA

        var newX = (data[0].x * relX) + (data[1].x * relY)
        var newY = (data[0].y * relX) + (data[1].y * relY)

        newX += originX
        newY += originY

        updatePosition(newX, newY)
    }
}