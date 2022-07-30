package io.github.hider.tilegame.entities

class Spike(initProps: EntityProps) : EntityWithHitbox(initProps), DeadlyEnemy {

    override var canCollide = false

    override fun update(deltaTime: Float) {/* Nothing to update */}
}
