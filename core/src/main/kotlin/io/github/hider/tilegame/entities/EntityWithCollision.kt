package io.github.hider.tilegame.entities

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.*
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.map.GameMap
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

sealed class EntityWithCollision(initProps: EntityProps, private val levelLoader: LevelLoader, private val map: GameMap): EntityWithHitbox(initProps) {
    protected var grounded = false
        private set
    protected var entityCollisionsLastFrame = mutableSetOf<EntityWithHitbox>()
        private set
    protected var mapCollisionLastFrame: Vector2 = Vector2.Zero
        private set

    override fun update(deltaTime: Float) {
        entityCollisionsLastFrame.clear()
        mapCollisionLastFrame = Vector2.Zero
        handleGravity(deltaTime)
    }

    protected fun handleBlockingCollision() {
        val newPosition = position + velocity

        val collisionMapY = mapCollisionY()
        val collisionEntityY = handleBlockingCollisionY(collisionMapY)
        val collisionY = if (collisionEntityY != null && collisionMapY != null) {
            if (abs(position.y - collisionEntityY) < abs(position.y - collisionMapY)) collisionEntityY else collisionMapY
        } else collisionEntityY ?: collisionMapY

        val collisionMapX = mapCollisionX()
        val collisionEntityX = handleBlockingCollisionX(collisionMapX)
        val collisionX = if (collisionEntityX != null && collisionMapX != null) {
            if (abs(position.x - collisionEntityX) < abs(position.x - collisionMapX)) collisionEntityX else collisionMapX
        } else collisionEntityX ?: collisionMapX

        mapCollisionLastFrame = Vector2(collisionMapX ?: 0f, collisionMapY ?: 0f)

        if (collisionY == null) {
            grounded = false
            position.y = newPosition.y
        } else {
            grounded = velocity.y.sign == GRAVITY.y.sign
            position.y = collisionY
            velocity.y = 0f
        }
        if (collisionX == null) {
            position.x = newPosition.x
        } else {
            position.x = collisionX
            velocity.x = 0f
        }
    }

    private fun handleGravity(deltaTime: Float) {
        velocity += GRAVITY * deltaTime
    }

    private fun mapCollisionY(): Float? {
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
                    map.findCollidedTilesWithMapByRect(toRectangle().setY(nextYToCheck))
                        .maxByOrNull { it.y }
                        ?.let {
                            collisionPos = it.y + it.height
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
                    map.findCollidedTilesWithMapByRect(toRectangle().setY(nextYToCheck))
                        .minByOrNull { it.y }
                        ?.let {
                            collisionPos = it.y - height
                        }
                    nextYToCheck += map.tileSize
                }
            } while (collisionPos == null && nextYToCheck <= newY)
        }

        return collisionPos
    }

    private fun mapCollisionX(): Float? {
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
                    map.findCollidedTilesWithMapByRect(toRectangle().setX(nextXToCheck))
                        .maxByOrNull { it.x }
                        ?.let {
                            collisionPos = it.x + it.width
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
                    map.findCollidedTilesWithMapByRect(toRectangle().setX(nextXToCheck))
                        .minByOrNull { it.x }
                        ?.let {
                            collisionPos = it.x - width
                        }
                    nextXToCheck += map.tileSize
                }
            } while (collisionPos == null && nextXToCheck <= newX)
        }

        return collisionPos
    }

    private fun hadCollisionY(startPos: Vector2, endPosY: Float, staticRect: Rectangle): Float? {
        val movingRect = Rectangle(startPos.x, startPos.y, width, height)
        val distance = endPosY - startPos.y
        movingRect.height += distance.absoluteValue
        // Going down
        if (distance < 0) {
            movingRect.y += distance
            val intersection = movingRect.intersection(staticRect)
            if (intersection != null) {
                return intersection.y + intersection.height
            }
            // Going up
        } else if (distance > 0) {
            val intersection = movingRect.intersection(staticRect)
            if (intersection != null) {
                return intersection.y - height
            }
        }
        return null
    }

    private fun hadCollisionX(startPos: Vector2, endPosX: Float, staticRect: Rectangle): Float? {
        val movingRect = Rectangle(startPos.x, startPos.y, width, height)
        val distance = endPosX - startPos.x
        movingRect.width += distance.absoluteValue
        // Moving left
        if (distance < 0) {
            movingRect.x += distance
            val intersection = movingRect.intersection(staticRect)
            if (intersection != null) {
                return intersection.x + intersection.width
            }
            // Moving right
        } else if (distance > 0) {
            val intersection = movingRect.intersection(staticRect)
            if (intersection != null) {
                return intersection.x - width
            }
        }
        return null
    }

    private fun handleBlockingCollisionY(collisionMapY: Float?): Float? {
        val level = levelLoader.currentLevel
        if (level != null) {
            level.entities.collidables
                .filter { it !== this }
                .sortedBy { abs(position.y - it.position.y) }
                .forEach { collidable ->
                    val newPos = hadCollisionY(collidable.toRectangle())
                    if (newPos != null) {
                        if (collisionMapY != null && abs(position.y - collisionMapY) < abs(position.y - newPos)) {
                            return null
                        }
                        entityCollisionsLastFrame.add(collidable)
                        if (collidable.canCollide) return newPos
                    }
                }
        }
        return null
    }

    private fun handleBlockingCollisionX(collisionMapX: Float?): Float? {
        val level = levelLoader.currentLevel
        if (level != null) {
            level.entities.collidables
                .filter { it !== this }
                .sortedBy { abs(position.x - it.position.x) }
                .forEach { collidable ->
                    val newPos = hadCollisionX(collidable.toRectangle())
                    if (newPos != null) {
                        if (collisionMapX != null && abs(position.x - collisionMapX) < abs(position.x - newPos)) {
                            return null
                        }
                        entityCollisionsLastFrame.add(collidable)
                        if (collidable.canCollide) return newPos
                    }
                }
        }
        return null
    }

    private fun hadCollisionY(staticRect: Rectangle) = hadCollisionY(position, position.y + velocity.y, staticRect)

    private fun hadCollisionX(staticRect: Rectangle) = hadCollisionX(position, position.x + velocity.x, staticRect)
}
