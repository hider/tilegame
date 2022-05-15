package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.map.GameMap

class NullEntity(map: GameMap): Entity(map) {
    override val position = Vector2(Vector2.Zero)
    override val height = 0f
    override val width = 0f

    override fun update(deltaTime: Float) {}
    override fun render(batch: Batch) {}
}
