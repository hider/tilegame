package io.github.hider.tilegame.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.GRAVITY
import io.github.hider.tilegame.io.InputHelpers.isPressed
import io.github.hider.tilegame.levels.CollectedEvent
import io.github.hider.tilegame.levels.LevelEndEvent
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.levels.PlayerDiedEvent
import io.github.hider.tilegame.map.GameMap
import io.github.hider.tilegame.plusAssign
import io.github.hider.tilegame.times
import kotlin.math.absoluteValue
import kotlin.math.sign

private enum class State {
    Idle, Walking, Jumping, Downed
}

private const val ACCELERATION = 10f
private const val MAX_SPEED = 5f
private val JUMP_VELOCITY = Vector2(GRAVITY).nor() * -5.2f
private const val FRICTION = 10

class Player(initProps: EntityProps, private val levelLoader: LevelLoader, map: GameMap): EntityWithCollision(initProps, levelLoader, map) {

    private var state = State.Idle
    private var flipX = false

    override fun render(batch: Batch) {
        val subtrahendX = if (flipX) {
            initProps.renderWidth - (initProps.hitbox.x + initProps.hitbox.width)
        } else {
            initProps.hitbox.x
        }
        val renderPos = Pair(position.x - subtrahendX, position.y - initProps.hitbox.y)
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
        super.update(deltaTime)
        if (state != State.Downed) {
            handleJump(deltaTime)
            handleMove(deltaTime)
        }
        handleFriction(deltaTime)
        val originalPosition = Vector2(position)
        val originalVelocity = Vector2(velocity)
        handleBlockingCollision()
        if (state != State.Downed) {
            handleInteractions(originalPosition, originalVelocity)
        }
        handleStateChange()
    }

    fun die(cause: EntityWithHitbox) {
        if (state != State.Downed) {
            levelLoader.currentLevel?.dispatchEvent(PlayerDiedEvent(cause))
            state = State.Downed
            canCollide = false
        }
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
            val jumpingModifier = if (grounded || directionX.sign != velocity.x.sign) 1f else .5f
            velocity.x += directionX * ACCELERATION * deltaTime * jumpingModifier
        }
        if (velocity.x.absoluteValue > MAX_SPEED) {
            velocity.x = MAX_SPEED * velocity.x.sign
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

    private fun handleInteractions(startPos: Vector2, originalVelocity: Vector2) {
        val level = levelLoader.currentLevel
        if (level != null && state != State.Downed) {
            entityCollisionsLastFrame.forEach {
                if (it is DeadlyEnemy) {
                    die(it)
                } else if (it is Collectible && !it.collected) {
                    level.dispatchEvent(CollectedEvent)
                    it.collected = true
                } else if (it is EndButton
                    && originalVelocity.y < 0
                    && startPos.y > it.position.y + it.height
                ) {
                    it.down = true
                    level.dispatchEvent(LevelEndEvent)
                }
            }
        }
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
