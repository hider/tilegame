package io.github.hider.tilegame.entities


class Entities(private val entities: List<Entity>) {

    val player : Player? = entities.filterIsInstance<Player>().firstOrNull()
    val collidables : List<EntityWithHitbox> = entities.filterIsInstance<EntityWithHitbox>()

    fun update(deltaTime: Float) {
        for (entity in entities) {
            entity.update(deltaTime)
        }
    }
}
