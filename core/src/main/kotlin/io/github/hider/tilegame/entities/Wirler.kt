package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.map.GameMap

private const val SPEED = 80f

class Wirler(
    private val initProps: EntityProps,
    private val levelLoader: LevelLoader,
    map: GameMap,
) : EntityWithCollision(initProps, levelLoader, map), DeadlyEnemy {

    private var rotation = 0f
    private var directionX = -1

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        velocity.x = SPEED * directionX * deltaTime
        handleBlockingCollision()
        handleInteractions()
        rotation -= velocity.x * 1.2f
    }

    override fun render(batch: Batch) {
        val renderPos =
            if (initProps.hitbox == null) position.x to position.y else position.x - initProps.hitbox.x to position.y - initProps.hitbox.y
        batch.draw(
            initProps.stateTexture.idle,
            renderPos.first,
            renderPos.second,
            initProps.renderWidth / 2,
            initProps.renderHeight / 2,
            initProps.renderWidth,
            initProps.renderHeight,
            1f,
            1f,
            rotation
        )
    }

    private fun handleInteractions() {
        val level = levelLoader.currentLevel
        var collided = mapCollisionLastFrame.x != 0f
        if (level != null) {
            level.entities.collidables
                .filter(entityCollisionsLastFrame::contains)
                .forEach {
                    if (it is Player) it.die(this)
                    else if (it.canCollide) collided = true
                }
        }
        if (collided) {
            directionX *= -1
        }
    }
}
