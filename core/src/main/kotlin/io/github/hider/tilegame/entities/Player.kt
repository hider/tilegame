package io.github.hider.tilegame.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.*
import io.github.hider.tilegame.io.InputHelpers.isPressed
import io.github.hider.tilegame.levels.*
import io.github.hider.tilegame.map.GameMap
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

private enum class State {
    Idle, Walking, Jumping, Downed
}

private const val ACCELERATION = 10f
private const val MAX_SPEED = 5f
private val JUMP_VELOCITY = Vector2(GRAVITY).nor() * -5.2f
private const val FRICTION = 10

class Player(private val initProps: EntityProps, private val levelLoader: LevelLoader, map: GameMap): EntityWithHitbox(initProps, map) {

    private var state = State.Idle
    private var flipX = false

    override var canCollide = false

    override fun render(batch: Batch) {
        val renderPos = if (initProps.hitbox == null) {
            Pair(position.x, position.y)
        } else {
            val subtrahendX = if (flipX) {
                initProps.renderWidth - (initProps.hitbox.x + initProps.hitbox.width)
            } else {
                initProps.hitbox.x
            }
            Pair(position.x - subtrahendX, position.y - initProps.hitbox.y)
        }
        val drawable = when (state) {
            State.Idle -> initProps.stateTexture.idle
            State.Walking -> initProps.stateTexture.walk()
            State.Jumping -> initProps.stateTexture.jump
            State.Downed -> initProps.stateTexture.down
        }
        val doFlipX = flipX && !drawable.isFlipX || !flipX && drawable.isFlipX
        drawable.flip(doFlipX, false)
        batch.draw(drawable, renderPos.first, renderPos.second, initProps.renderWidth, initProps.renderHeight)
    }

    override fun update(deltaTime: Float) {
        handleGravity(deltaTime)
        if (state != State.Downed) {
            handleJump(deltaTime)
            handleMove(deltaTime)
        }
        handleFriction(deltaTime)
        val originalPosition = Vector2(position)
        handleBlockingCollision()
        if (state != State.Downed) {
            handleInteractions(originalPosition, Vector2(position.x, position.y))
        }
        handleStateChange()
    }

    private fun handleGravity(deltaTime: Float) {
        velocity += GRAVITY * deltaTime
    }

    private fun handleJump(deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Keys.SPACE) && grounded) {
            velocity += JUMP_VELOCITY
        } else if (Gdx.input.isKeyPressed(Keys.SPACE) && !grounded && velocity.y > 0) {
            velocity += JUMP_VELOCITY * deltaTime
        }
    }

    private fun handleMove(deltaTime: Float) {
        var directionX = 0f
        // Move left
        if (isPressed(Keys.LEFT, Keys.A)) {
            directionX = -1f
            flipX = true
        }
        // Move right
        if (isPressed(Keys.RIGHT, Keys.D)) {
            directionX = 1f
            flipX = false
        }

        if (directionX != 0f) {
            val jumpingModifier = if (grounded) 1f else .75f
            velocity.x += directionX * ACCELERATION * deltaTime * jumpingModifier
            if (velocity.x.absoluteValue > MAX_SPEED) {
                velocity.x = MAX_SPEED * velocity.x.sign
            }
        }
    }

    private fun handleFriction(deltaTime: Float) {
        if (velocity.x != 0f && !isPressed(Keys.LEFT, Keys.A, Keys.RIGHT, Keys.D)) {
            // When moving right
            if (velocity.x > 0) {
                velocity.x -= FRICTION * deltaTime
                if (velocity.x < 0) {
                    velocity.x = 0f
                }
            }
            // When moving left
            if (velocity.x < 0) {
                velocity.x += FRICTION * deltaTime
                if (velocity.x > 0) {
                    velocity.x = 0f
                }
            }
        }
    }

    private fun handleInteractions(startPos: Vector2, endPos: Vector2) {
        val level = levelLoader.currentLevel
        if (level != null) {
            level.entities.collidables
                .filter {
                    (it is Spike || it is Collectible && !it.collected) && hadAnyCollision(startPos, endPos, it.toRectangle())
                }.forEach {
                    if (it is Spike) {
                        level.dispatchEvent(PlayerDiedEvent(it))
                        state = State.Downed
                    } else if (it is Collectible) {
                        level.dispatchEvent(CollectedEvent)
                        it.collected = true
                    }
                }
            val endGameButton = level.entities.collidables.filterIsInstance<EndButton>().firstOrNull()
            if (endGameButton != null
                && velocity.y < 0
                && hadCollisionY(startPos, endPos.y - endGameButton.height, endGameButton.toRectangle()) != null
                && startPos.y > endGameButton.position.y + endGameButton.height
            ) {
                endGameButton.down = true
                level.dispatchEvent(LevelEndEvent)
            }
        }
    }

    private fun handleBlockingCollision() {
        val newPosition = position + velocity

        val collisionY1 = handleBlockingCollisionY()
        val collisionY2 = mapCollisionY()
        val collisionY = if (collisionY1 != null && collisionY2 != null) {
            if (abs(position.y - collisionY1) < abs(position.y - collisionY2)) collisionY1 else collisionY2
        } else collisionY1 ?: collisionY2

        val collisionX1 = handleBlockingCollisionX()
        val collisionX2 = mapCollisionX()
        val collisionX = if (collisionX1 != null && collisionX2 != null) {
            if (abs(position.x - collisionX1) < abs(position.x - collisionX2)) collisionX1 else collisionX2
        } else collisionX1 ?: collisionX2

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

    private fun handleBlockingCollisionY(): Float? {
        val level = levelLoader.currentLevel
        if (level != null) {
            level.entities.collidables
                .filter { it.canCollide }
                .sortedBy { abs(position.y - it.position.y) }
                .forEach { block ->
                    val newPos = hadCollisionY(block.toRectangle())
                    if (newPos != null) return newPos
                }
        }
        return null
    }

    private fun hadAnyCollision(startPos: Vector2, endPos: Vector2, staticRect: Rectangle): Boolean {
        return hadCollisionX(startPos, endPos.x, staticRect) != null || hadCollisionY(startPos, endPos.y, staticRect) != null
    }

    private fun hadCollisionY(staticRect: Rectangle) = hadCollisionY(position, position.y + velocity.y, staticRect)

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

    private fun handleBlockingCollisionX(): Float? {
        val level = levelLoader.currentLevel
        if (level != null) {
            level.entities.collidables
                .filter { it.canCollide }
                .sortedBy { abs(position.x - it.position.x) }
                .forEach { block ->
                if (block.canCollide) {
                    val newPos = hadCollisionX(block.toRectangle())
                    if (newPos != null) {
                        return newPos
                    }
                }
            }
        }
        return null
    }

    private fun hadCollisionX(staticRect: Rectangle) = hadCollisionX(position, position.x + velocity.x, staticRect)

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

    private fun handleStateChange() {
        if (state != State.Downed) {
            state = if (grounded && velocity.x != 0f) {
                State.Walking
            } else if (!grounded) {
                State.Jumping
            } else {
                State.Idle
            }
        }
    }
}
