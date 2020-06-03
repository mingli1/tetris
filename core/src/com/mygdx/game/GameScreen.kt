package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport

const val PPS = 0
const val APM = 1
const val ATT = 2
const val LINES_SENT = 3
const val COMBO = 4
const val B2B = 5
const val TSS = 6
const val TSD = 7
const val TST = 8
const val SINGLE = 9
const val DOUBLE = 10
const val TRIPLE = 11
const val QUAD = 12
const val PC = 13

private const val V_WIDTH = 800f
private const val V_HEIGHT = 800f

class GameScreen(private val game: Tetris) : Screen {

    private val cam = OrthographicCamera().apply {
        setToOrtho(false, V_WIDTH, V_HEIGHT)
    }
    private lateinit var grid: Grid
    private lateinit var inputHandler: InputHandler
    private lateinit var stage: Stage
    private lateinit var viewport: Viewport

    private val statStrings = listOf(
        "PPS",
        "APM",
        "ATT",
        "LINES SENT",
        "COMBO",
        "B2B",
        "TSS",
        "TSD",
        "TST",
        "SINGLE",
        "DOUBLE",
        "TRIPLE",
        "QUAD",
        "PC"
    )
    private val labelStyle = Label.LabelStyle(BitmapFont(), Color.WHITE)
    private val statsLabels = statStrings.mapIndexed { index, s ->
        Label(s, labelStyle).apply { setPosition(100f, 500f - index * 32) }
    }

    override fun show() {
        viewport = ExtendViewport(V_WIDTH, V_HEIGHT, cam)
        stage = Stage(viewport, game.batch)

        grid = Grid(10, 20, V_WIDTH / 2 - (10 * 32 / 2), 50f, game.res)
        inputHandler = InputHandler(grid)

        Gdx.input.inputProcessor = inputHandler

        statsLabels.forEach { stage.addActor(it) }
    }

    private fun update(dt: Float) {
        inputHandler.update(dt)
        grid.update(dt)

        for (i in statsLabels.indices) {
            statsLabels[i].setText(grid.stats[i].toString() + " " + statStrings[i])
        }
    }

    override fun render(dt: Float) {
        update(dt)
        Gdx.gl.glClearColor(30 / 255f, 30 / 255f, 30 / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        grid.render(game.batch)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}