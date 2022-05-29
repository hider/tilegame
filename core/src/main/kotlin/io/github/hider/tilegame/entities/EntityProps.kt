package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

data class EntityProps(
    val id: Int,
    val position: Vector2,
    val stateTexture: EntityStateTexture,
    val renderWidth: Float,
    val renderHeight: Float,
    val hitbox: Rectangle?,
    val flip: Flip
) {
    data class EntityStateTexture(
        val idle: TextureRegion,
        val walk: () -> TextureRegion,
        val jump: TextureRegion,
        val down: TextureRegion,
    )
    data class Flip(val vertical: Boolean, val horizontal: Boolean)
}
