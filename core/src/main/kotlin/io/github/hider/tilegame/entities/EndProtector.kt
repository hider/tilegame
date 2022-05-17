package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import io.github.hider.tilegame.map.GameMap

class EndProtector(initProps: EntityProps, map: GameMap): EntityWithHitbox(initProps, map) {

    override fun update(deltaTime: Float) {}
    override fun render(batch: Batch) {
        if (canCollide) {
            super.render(batch)
        }
    }
}
