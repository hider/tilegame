package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import io.github.hider.tilegame.map.GameMap

class CollidingEntity(initProps: EntityProps, map: GameMap): EntityWithHitbox(initProps, map) {

    var canCollide = true

    override fun update(deltaTime: Float) {}
    override fun render(batch: Batch) {
        if (canCollide) {
            super.render(batch)
        }
    }
}
