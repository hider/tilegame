package io.github.hider.tilegame.screens.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.hider.tilegame.TileGame

class DebugText(private val game: TileGame, vararg texts: String) {

    private val texts = texts.toMutableList()

    fun add(text: String) {
        texts.add(text)
    }

    fun render(viewport: Viewport) {
        val textPos = viewport.camera.unproject(Vector3(5f, Gdx.graphics.height - 5f, 0f))
        for ((textLevel, text) in texts.withIndex()) {
            val x = textPos.x
            val y = Gdx.graphics.height + textPos.y - 5f * 2 - game.fonts.default.capHeight * textLevel - 5 * textLevel - viewport.topGutterHeight - viewport.bottomGutterHeight
            game.fonts.default.draw(game.batch, text, x, y)
        }
    }
}
