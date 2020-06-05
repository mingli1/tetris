package com.mygdx.game

enum class Rotation {
    Clockwise,
    Counterclockwise,
    OneEighty;

    fun opposite() = when (this) {
        Clockwise -> Counterclockwise
        Counterclockwise -> Clockwise
        OneEighty -> OneEighty
    }
}