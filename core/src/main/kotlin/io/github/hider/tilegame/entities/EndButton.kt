package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import io.github.hider.tilegame.map.GameMap

class EndButton(private val initProps: EntityProps, map: GameMap): EntityWithHitbox(initProps, map) {

    var down = false

    override fun update(deltaTime: Float) {}
    override fun render(batch: Batch) {
        if (down) {
            val renderPos = if (initProps.hitbox == null) position.x to position.y else position.x - initProps.hitbox.x to position.y - initProps.hitbox.y
            batch.draw(initProps.stateTexture.down, renderPos.first, renderPos.second, initProps.renderWidth, initProps.renderHeight)
        } else {
            super.render(batch)
        }
    }
}
