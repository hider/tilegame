package io.github.hider.tilegame.entities

import io.github.hider.tilegame.map.GameMap

class Spike(
    initProps: EntityProps,
    map: GameMap,
) : EntityWithHitbox(initProps, map), DeadlyEnemy {

    override var canCollide = false

    override fun update(deltaTime: Float) {/* Nothing to update */}
}
