package io.github.hider.tilegame.screens.ui

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import io.github.hider.tilegame.screens.ui.UiMessages.UiMessageLocation

class UiMessages(private val camera: Camera) {
    private var messages = mutableSetOf<UiMessageState>()

    fun add(text: String, font: BitmapFont, waitTimeInMillis: Long, location: UiMessageLocation = UiMessageLocation.Center) {
        messages.add(UiMessageState(text, font, location, System.currentTimeMillis() + waitTimeInMillis))
    }

    fun update() {
        messages.removeIf {
            it.showUntilMillis < System.currentTimeMillis()
        }
    }

    fun render(batch: Batch) {
        val zero = camera.unproject(Vector3(0f, 0f, 0f))
        messages.forEach {
            // Color is mutable :(
            val origColor = Color(it.font.color)
            it.font.color = Color.YELLOW
            val layout = GlyphLayout(it.font, it.text)
            val pos = when (it.location) {
                UiMessageLocation.TopLeft -> topLeft(zero)
                UiMessageLocation.BottomLeft -> bottomLeft(zero, layout)
                UiMessageLocation.Center -> center(zero, layout, it.font)
            }
            it.font.draw(batch, layout, pos.x, pos.y)
            it.font.color = origColor
        }
    }

    private fun topLeft(zero: Vector3): Vector2 {
        val x = zero.x + 25
        val y = zero.y
        return Vector2(x, y)
    }

    private fun bottomLeft(zero: Vector3, layout: GlyphLayout): Vector2 {
        val x = zero.x + 25
        val y = zero.y - camera.viewportHeight + layout.height + 25
        return Vector2(x, y)
    }

    private fun center(zero: Vector3, layout: GlyphLayout, font: BitmapFont): Vector2 {
        val x = zero.x + (camera.viewportWidth - layout.width) / 2
        val y = zero.y - (camera.viewportHeight - layout.height - font.capHeight) / 2
        return Vector2(x, y)
    }

    enum class UiMessageLocation {
        TopLeft, BottomLeft, Center
    }
}

private data class UiMessageState(
    val text: String,
    val font: BitmapFont,
    val location: UiMessageLocation,
    val showUntilMillis: Long,
)
