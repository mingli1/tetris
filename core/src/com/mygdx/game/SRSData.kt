package com.mygdx.game

val CW_ROTATE_DATA = arrayOf(
    Vector2Int(0, -1),
    Vector2Int(1, 0)
)

val CCW_ROTATE_DATA = arrayOf(
    Vector2Int(0, 1),
    Vector2Int(-1, 0)
)

val TSZLJ_OFFSET_DATA = arrayOf(
    arrayOf(Vector2Int(0, 0), Vector2Int(0, 0), Vector2Int(0, 0), Vector2Int(0, 0)),
    arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(0, 0), Vector2Int(-1, 0)),
    arrayOf(Vector2Int(0, 0), Vector2Int(1, -1), Vector2Int(0, 0), Vector2Int(-1, -1)),
    arrayOf(Vector2Int(0, 0), Vector2Int(0, 2), Vector2Int(0, 0), Vector2Int(0, 2)),
    arrayOf(Vector2Int(0, 0), Vector2Int(1, 2), Vector2Int(0, 0), Vector2Int(-1, 2))
)

val I_OFFSET_DATA = arrayOf(
    arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(-1, 1), Vector2Int(0, 1)),
    arrayOf(Vector2Int(-1, 0), Vector2Int(0, 0), Vector2Int(1, 1), Vector2Int(0, 1)),
    arrayOf(Vector2Int(2, 0), Vector2Int(0, 0), Vector2Int(-2, 1), Vector2Int(0, 1)),
    arrayOf(Vector2Int(-1, 0), Vector2Int(0, 1), Vector2Int(1, 0), Vector2Int(0, -1)),
    arrayOf(Vector2Int(2, 0), Vector2Int(0, -2), Vector2Int(-2, 0), Vector2Int(0, 2))
)