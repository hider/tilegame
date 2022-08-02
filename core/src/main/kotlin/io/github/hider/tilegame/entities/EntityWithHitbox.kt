package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

sealed class EntityWithHitbox(initProps: EntityProps): Entity(initProps) {

    open var textureRegion: TextureRegion = initProps.stateTexture.idle
    open var canCollide = true
    final override val height: Float
    final override val width: Float

    init {
        if (initProps.flip.vertical) {
            super.position.add(initProps.hitbox.x, initProps.renderHeight - initProps.hitbox.y - initProps.hitbox.height)
        } else {
            super.position.add(initProps.hitbox.x, initProps.hitbox.y)
        }
        height = initProps.hitbox.height
        width = initProps.hitbox.width
    }

    override fun render(batch: Batch) {
        val renderPos = position.x - initProps.hitbox.x to position.y - initProps.hitbox.y + if (initProps.flip.vertical) {
            initProps.hitbox.height
        } else 0f
        val renderHeight = initProps.renderHeight * if (initProps.flip.vertical) -1 else 1
        batch.draw(textureRegion, renderPos.first, renderPos.second, initProps.renderWidth, renderHeight)
    }
}
