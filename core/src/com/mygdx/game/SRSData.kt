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

/*
val TSZLJ_OFFSET_DATA = arrayOf(
    // O
    arrayOf(
        // R
        arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(1, 1), Vector2Int(0, -2), Vector2Int(1, -2)),
        // L
        arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(-1, 1), Vector2Int(0, -2), Vector2Int(-1, -2))
    ),
    // L
    arrayOf(
        // O
        arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(1, -1), Vector2Int(0, 2), Vector2Int(1, 2)),
        // 2
        arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(1, -1), Vector2Int(0, 2), Vector2Int(1, 2))
    ),
    // 2
    arrayOf(
        // L
        arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(-1, 1), Vector2Int(0, -2), Vector2Int(-1, -2)),
        // R
        arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(1, 1), Vector2Int(0, -2), Vector2Int(1, -2))
    ),
    // R
    arrayOf(
        // 2
        arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(-1, -1), Vector2Int(0, 2), Vector2Int(-1, 2)),
        // O
        arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(-1, -1), Vector2Int(0, 2), Vector2Int(-1, 2))
    )
)

val I_OFFSET_DATA = arrayOf(
    // O
    arrayOf(
        // R
        arrayOf(Vector2Int(0, 0), Vector2Int(2, 0), Vector2Int(-1, 0), Vector2Int(2, -1), Vector2Int(-1, 2)),
        // L
        arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(-2, 0), Vector2Int(1, 2), Vector2Int(-2, -1))
    ),
    // L
    arrayOf(
        // O
        arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(2, 0), Vector2Int(-1, -2), Vector2Int(2, 1)),
        // 2
        arrayOf(Vector2Int(0, 0), Vector2Int(2, 0), Vector2Int(-1, 0), Vector2Int(2, -1), Vector2Int(-1, 2))
    ),
    // 2
    arrayOf(
        // L
        arrayOf(Vector2Int(0, 0), Vector2Int(-2, 0), Vector2Int(1, 0), Vector2Int(-2, 1), Vector2Int(1, -2)),
        // R
        arrayOf(Vector2Int(0, 0), Vector2Int(-1, 0), Vector2Int(2, 0), Vector2Int(-1, -2), Vector2Int(2, 1))
    ),
    // R
    arrayOf(
        // 2
        arrayOf(Vector2Int(0, 0), Vector2Int(1, 0), Vector2Int(-2, 0), Vector2Int(1, 2), Vector2Int(-2, -1)),
        // O
        arrayOf(Vector2Int(0, 0), Vector2Int(-2, 0), Vector2Int(1, 0), Vector2Int(-2, 1), Vector2Int(1, -2))
    )
)
 */