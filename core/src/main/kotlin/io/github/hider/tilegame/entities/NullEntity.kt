package io.github.hider.tilegame.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

private val textureRegion = TextureRegion()

private val nullProps = EntityProps(
    0,
    Vector2(Vector2.Zero),
    EntityProps.EntityStateTexture(textureRegion, { textureRegion }, textureRegion, textureRegion),
    0f,
    0f,
    Rectangle(),
    EntityProps.Flip(vertical = false, horizontal = false)
)

class NullEntity(gid: Int): Entity(nullProps) {

    init {
        Gdx.app.log("NullEntity", "WARN: NullEntity just created from gid #$gid")
    }

    override fun update(deltaTime: Float) {/* Nothing to update */}
    override fun render(batch: Batch) {/* Nothing to render */}
}
