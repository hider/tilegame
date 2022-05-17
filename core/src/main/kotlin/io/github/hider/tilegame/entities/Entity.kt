package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.map.GameMap
import kotlin.math.absoluteValue
import kotlin.math.min

sealed class Entity(private val map: GameMap) {

    protected var grounded = false
    protected val velocity = Vector2()

    abstract val position: Vector2
    abstract val height: Float
    abstract val width: Float

    abstract fun update(deltaTime: Float)
    abstract fun render(batch: Batch)

    fun createSnapshot(): EntitySnapshot = EntitySnapshot(this.javaClass.name, position.x, position.y)

    fun toRectangle() = Rectangle(position.x, position.y, width, height)

    protected fun mapCollisionY(): Float? {
        val newY = position.y + velocity.y
        val distanceTraveled = (position.y - newY).absoluteValue
        val stepSize = min(distanceTraveled, map.tileSize.toFloat())
        var collisionPos: Float? = null
        // Going down
        if (velocity.y < 0) {
            var nextYToCheck = position.y - stepSize
            // Check every tile between the current pos and the target pos by going down
            do {
                // Check if out of the map
                if (nextYToCheck < 0) {
                    collisionPos = 0f
                } else {
                    val intersection = map.findIntersectionWithMapByRect(toRectangle().setY(nextYToCheck))
                    if (intersection != null) {
                        collisionPos = intersection.y + intersection.height
                    }
                    nextYToCheck -= map.tileSize
                }
            } while (collisionPos == null && nextYToCheck >= newY)
        // Going up
        } else if (velocity.y > 0) {
            var nextYToCheck = position.y + stepSize
            // Check every tile between the current pos and the target pos by going up
            do {
                // Check if out of the map
                if (nextYToCheck + height > map.getPixelHeight()) {
                    collisionPos = map.getPixelHeight() - height
                } else {
                    val intersection = map.findIntersectionWithMapByRect(toRectangle().setY(nextYToCheck))
                    if (intersection != null) {
                        collisionPos = intersection.y - height
                    }
                    nextYToCheck += map.tileSize
                }
            } while (collisionPos == null && nextYToCheck <= newY)
        }

        return collisionPos
    }

    protected fun mapCollisionX(): Float? {
        val newX = position.x + velocity.x
        val distanceTraveled = (position.x - newX).absoluteValue
        val stepSize = min(distanceTraveled, map.tileSize.toFloat())
        var collisionPos: Float? = null
        // Moving left
        if (velocity.x < 0) {
            var nextXToCheck = position.x - stepSize
            // Check every tile between the current pos and the target pos by moving left
            do {
                // Check if out of the map
                if (nextXToCheck < 0) {
                    collisionPos = 0f
                } else {
                    val intersection = map.findIntersectionWithMapByRect(toRectangle().setX(nextXToCheck))
                    if (intersection != null) {
                        collisionPos = intersection.x + intersection.width
                    }
                    nextXToCheck -= map.tileSize
                }
            } while (collisionPos == null && nextXToCheck >= newX)
        // Moving right
        } else if (velocity.x > 0) {
            var nextXToCheck = position.x + stepSize
            // Check every tile between the current pos and the target pos by moving right
            do {
                // Check if out of the map
                if (nextXToCheck + width > map.getPixelWidth()) {
                    collisionPos = map.getPixelWidth() - width
                } else {
                    val intersection = map.findIntersectionWithMapByRect(toRectangle().setX(nextXToCheck))
                    if (intersection != null) {
                        collisionPos = intersection.x - width
                    }
                    nextXToCheck += map.tileSize
                }
            } while (collisionPos == null && nextXToCheck <= newX)
        }

        return collisionPos
    }
}
