package io.github.hider.tilegame.entities

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.*
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.map.GameMap
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

sealed class EntityWithCollision(initProps: EntityProps, private val levelLoader: LevelLoader, map: GameMap): EntityWithHitbox(initProps, map) {
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
