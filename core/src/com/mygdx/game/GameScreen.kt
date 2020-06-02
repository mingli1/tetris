package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera

private const val V_WIDTH = 800f
private const val V_HEIGHT = 800f

class GameScreen(private val game: Tetris) : Screen {

    private val cam = OrthographicCamera().apply {
        setToOrtho(false, V_WIDTH, V_HEIGHT)
    }
    private lateinit var grid: Grid
    private lateinit var inputHandler: InputHandler

    override fun show() {
        grid = Grid(10, 20, V_WIDTH / 2 - (10 * 32 / 2), 50f, game.res)
        inputHandler = InputHandler(grid)

        Gdx.input.inputProcessor = inputHandler
    }

    private fun update(dt: Float) {
        inputHandler.update(dt)
    }

    override fun render(dt: Float) {
        update(dt)
        Gdx.gl.glClearColor(30 / 255f, 30 / 255f, 30 / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        grid.render(game.batch)

        game.batch.end()
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
    }
}