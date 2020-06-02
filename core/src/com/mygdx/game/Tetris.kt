package com.mygdx.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Tetris : Game() {

    lateinit var batch: Batch
    lateinit var res: Resources

    private lateinit var gameScreen: GameScreen

    override fun create() {
        batch = SpriteBatch()
        res = Resources()

        gameScreen = GameScreen(this)
        setScreen(gameScreen)
    }

    override fun render() {
        Gdx.graphics.setTitle("${Gdx.graphics.framesPerSecond} fps")
        super.render()
    }

    override fun dispose() {
        batch.dispose()
        res.dispose()
        gameScreen.dispose()
    }
}