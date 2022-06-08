package io.github.hider.tilegame.screens.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import io.github.hider.tilegame.entities.Collectible
import io.github.hider.tilegame.entities.Spike
import io.github.hider.tilegame.io.Fonts
import io.github.hider.tilegame.levels.CollectedEvent
import io.github.hider.tilegame.levels.Level
import io.github.hider.tilegame.levels.PlayerDiedEvent
import io.github.hider.tilegame.use
import java.lang.Float.min


private const val MESSAGE_VISIBLE_MILLIS = 5000L

class Hud(private val camera: Camera, private val fonts: Fonts, level: Level) {

    private val shapeRenderer = ShapeRenderer().apply {
        color = Color.valueOf("#34deeb").apply { a = .2f }
    }
    private val collectibleCount = level.entities.collidables.filterIsInstance<Collectible>().size
    private val collectibleTexture = if (collectibleCount > 0) level.entities.collidables.filterIsInstance<Collectible>()[0].texture else null
    private val messages = UiMessages(camera)
    private var lastMessageMillis = 0L
    private var collectedCount = 0

    init {
        level.subscribeEvent(PlayerDiedEvent::class) {
            if (it.cause is Spike && lastMessageMillis + MESSAGE_VISIBLE_MILLIS < System.currentTimeMillis()) {
                addMessage("Touching the spikes is dangerous!", fonts.sansSerif22, UiMessages.UiMessageLocation.BottomLeft)
                lastMessageMillis = System.currentTimeMillis()
            }
        }
        level.subscribeEvent(CollectedEvent::class) {
            ++collectedCount
        }
    }

    fun addMessage(text: String, font: BitmapFont, location: UiMessages.UiMessageLocation) {
        messages.add(text, font, MESSAGE_VISIBLE_MILLIS, location)
    }

    fun update() {
        messages.update()
    }

    fun render(batch: Batch) {
        if (collectibleCount == 0) return

        batch.projectionMatrix = camera.combined

        val zero = camera.unproject(Vector3(0f, 0f, 0f))
        val layout = if (collectibleTexture == null) {
            GlyphLayout(fonts.sansSerif22, "Collected $collectedCount/$collectibleCount")
        } else {
            GlyphLayout(fonts.sansSerif22, ": $collectedCount/$collectibleCount")
        }
        val fontX = zero.x + (camera.viewportWidth - layout.width) / 2
        val fontY = zero.y - layout.height

        if (collectibleTexture != null) {
            val shapeX = min(fontX - 10, fontX - collectibleTexture.regionWidth)
            val shapeY = min(fontY - 10, fontY - collectibleTexture.regionHeight + layout.height)
            val shapeWidth = layout.width + collectibleTexture.regionWidth.toFloat()
            val shapeHeight = layout.height + collectibleTexture.regionHeight.toFloat()
            renderShape(shapeX, shapeY, shapeWidth + 20, shapeHeight + 20)
        } else {
            renderShape(fontX - 10, fontY - layout.height - 10, layout.width + 20, layout.height + 20)
        }
        batch.use {
            if (collectibleTexture != null) {
                batch.draw(collectibleTexture,
                    fontX - collectibleTexture.regionWidth,
                    fontY - collectibleTexture.regionHeight + layout.height,
                    collectibleTexture.regionWidth.toFloat(),
                    collectibleTexture.regionHeight.toFloat()
                )
            }
            fonts.sansSerif22.draw(batch, layout, fontX, fontY)
            messages.render(batch)
        }
    }

    /**
     * See https://gamedev.stackexchange.com/a/118396
     */
    private fun renderShape(x: Float, y: Float, width: Float, height: Float) {
        shapeRenderer.projectionMatrix = camera.combined
        val radius = 7f

        val blendEnabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND)
        if (!blendEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        // Central rectangle
        shapeRenderer.rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius)

        // Four side rectangles, in clockwise order
        shapeRenderer.rect(x + radius, y, width - 2 * radius, radius)
        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2 * radius)
        shapeRenderer.rect(x + radius, y + height - radius, width - 2 * radius, radius)
        shapeRenderer.rect(x, y + radius, radius, height - 2 * radius)

        // Four arches, clockwise too
        shapeRenderer.arc(x + radius, y + radius, radius, 180f, 90f)
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270f, 90f)
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0f, 90f)
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90f, 90f)
        shapeRenderer.end()
        if (!blendEnabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
    }
}
