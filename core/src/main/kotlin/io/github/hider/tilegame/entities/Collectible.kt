package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import io.github.hider.tilegame.map.GameMap

class Collectible(initProps: EntityProps, map: GameMap): EntityWithHitbox(initProps, map) {

    val texture = initProps.stateTexture.idle
    var collected = false

    override fun update(deltaTime: Float) {}
    override fun render(batch: Batch) {
        if (!collected) {
            super.render(batch)
        }
    }
}
