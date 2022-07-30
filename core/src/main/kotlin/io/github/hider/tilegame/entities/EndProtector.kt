package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch

class EndProtector(initProps: EntityProps): EntityWithHitbox(initProps) {

    override fun update(deltaTime: Float) {/* Nothing to update */}
    override fun render(batch: Batch) {
        if (canCollide) {
            super.render(batch)
        }
    }
}
