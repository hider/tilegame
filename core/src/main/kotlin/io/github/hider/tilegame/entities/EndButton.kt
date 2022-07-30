package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch

class EndButton(private val initProps: EntityProps): EntityWithHitbox(initProps) {

    var down = false

    override fun update(deltaTime: Float) {/* Nothing to update */}
    override fun render(batch: Batch) {
        if (down) {
            val renderPos = if (initProps.hitbox == null) position.x to position.y else position.x - initProps.hitbox.x to position.y - initProps.hitbox.y
            batch.draw(initProps.stateTexture.down, renderPos.first, renderPos.second, initProps.renderWidth, initProps.renderHeight)
        } else {
            super.render(batch)
        }
    }
}
