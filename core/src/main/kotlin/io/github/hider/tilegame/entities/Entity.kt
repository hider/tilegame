package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

sealed class Entity {

    protected val velocity = Vector2()

    abstract val position: Vector2
    abstract val height: Float
    abstract val width: Float

    abstract fun update(deltaTime: Float)
    abstract fun render(batch: Batch)

    fun createSnapshot(): EntitySnapshot = EntitySnapshot(this.javaClass.name, position.x, position.y)

    fun toRectangle() = Rectangle(position.x, position.y, width, height)
}
