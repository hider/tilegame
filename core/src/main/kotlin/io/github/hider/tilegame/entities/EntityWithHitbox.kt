package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.map.GameMap

sealed class EntityWithHitbox(private val initProps: EntityProps, map: GameMap): Entity(map) {

    final override val position: Vector2
    final override val height: Float
    final override val width: Float

    open var canCollide = true

    init {
        if (initProps.hitbox == null) {
            position = Vector2(initProps.position)
            height = initProps.renderHeight
            width = initProps.renderWidth
        } else {
            position = Vector2(initProps.position).add(initProps.hitbox.x, initProps.hitbox.y)
            height = initProps.hitbox.height
            width = initProps.hitbox.width
        }
    }

    override fun render(batch: Batch) {
        val renderPos = if (initProps.hitbox == null) position.x to position.y else position.x - initProps.hitbox.x to position.y - initProps.hitbox.y
        batch.draw(initProps.stateTexture.idle, renderPos.first, renderPos.second, initProps.renderWidth, initProps.renderHeight)
    }
}
