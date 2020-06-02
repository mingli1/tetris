package com.mygdx.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas

    private val squaresTexture: Array<TextureRegion>
    private val ghostTexture: Array<TextureRegion>

    init {
        assetManager.load("textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures.atlas", TextureAtlas::class.java)

        squaresTexture = getTexture("squares").split(32, 32)[0]
        ghostTexture = getTexture("squares").split(32, 32)[1]
    }

    fun getTexture(key: String): TextureRegion =
        checkNotNull(atlas.findRegion(key))

    fun getSquare(type: PieceType) = squaresTexture[type.index]

    fun getGhost(type: PieceType) = ghostTexture[type.index - 2]

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
    }
}