package io.github.hider.tilegame.screens.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import io.github.hider.tilegame.entities.Entity
import io.github.hider.tilegame.use

class IdentityHashCodeRenderer(private val entities: Collection<Entity>, private val font: BitmapFont) {
    fun render(batch: Batch) = batch.use {
        entities.forEach {
            val text = Integer.toHexString(System.identityHashCode(it))
            val layout = GlyphLayout(font, text)
            val x = it.position.x + it.width / 2 - layout.width / 2
            val y = it.position.y + it.height / 2 - layout.height / 2
            font.draw(batch, text, x, y)
        }
    }
}
