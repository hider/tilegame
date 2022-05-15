package io.github.hider.tilegame.entities


class Entities(private val entities: List<Entity>) {

    val player : Player? = entities.filterIsInstance<Player>().firstOrNull()
    val spikes : List<Spike> = entities.filterIsInstance<Spike>()
    val collectibles : List<Collectible> = entities.filterIsInstance<Collectible>()
    val entityBlocks : List<CollidingEntity> = entities.filterIsInstance<CollidingEntity>()
    val specials : List<EndButton> = entities.filterIsInstance<EndButton>()

    fun update(deltaTime: Float) {
        for (entity in entities) {
            entity.update(deltaTime)
        }
    }
}
