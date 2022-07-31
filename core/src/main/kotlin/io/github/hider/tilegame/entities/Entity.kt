package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

sealed class Entity(protected val initProps: EntityProps) {

    protected val velocity = Vector2()

    val position = Vector2(initProps.position)
    open val height = initProps.renderHeight
    open val width = initProps.renderWidth

    abstract fun update(deltaTime: Float)
    abstract fun render(batch: Batch)

    fun createSnapshot(): EntitySnapshot = EntitySnapshot(this.javaClass.name, position.x, position.y)

    fun toRectangle() = Rectangle(position.x, position.y, width, height)
}
