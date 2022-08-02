package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch

class Collectible(initProps: EntityProps): EntityWithHitbox(initProps) {

    var collected = false

    override var canCollide = false

    override fun update(deltaTime: Float) {/* Nothing to update */}
    override fun render(batch: Batch) {
        if (!collected) {
            super.render(batch)
        }
    }
}
