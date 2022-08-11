package io.github.hider.tilegame.levels

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import io.github.hider.tilegame.FatalGameException
import io.github.hider.tilegame.entities.*
import io.github.hider.tilegame.map.TiledGameMap
import io.github.hider.tilegame.map.TiledMapEntityObject
import io.github.hider.tilegame.map.TmxConfig
import io.github.hider.tilegame.set
import io.github.hider.tilegame.sort
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import kotlin.reflect.KClass

class LevelLoader(levelsPath: String) : Disposable {
    private val log = LoggerFactory.getLogger(this.javaClass)

    var currentLevel: Level? = null
        private set
    private val levels: List<LevelData>
    private val stateTiles = mutableMapOf<Int, TiledMapTile>()

    init {
        try {
            val file = Gdx.files.internal(levelsPath)
            @OptIn(ExperimentalSerializationApi::class)
            levels = Json.decodeFromStream(file.read())
        } catch (e: Exception) {
            val message = "Unable to load game levels from $levelsPath"
            log.error(message, e)
            throw FatalGameException(message, e)
        }
    }

    fun levels() = levels.map { it.name }

    fun load(levelName: String, batch: Batch): Level {
        var lvl = currentLevel
        if (lvl != null && lvl.name == levelName) {
            return lvl
        }
        val data = levels.first { it.name == levelName }
        lvl?.dispose()
        lvl = createLevel(batch, data)
        currentLevel = lvl
        return lvl
    }

    fun reset() {
        dispose()
        currentLevel = null
    }

    override fun dispose() {
        currentLevel?.dispose()
    }

    private fun createLevel(batch: Batch, data: LevelData): Level {
        val map = TiledGameMap(batch, data.tmx)
        stateTiles.clear()
        map.tiledMap.tileSets
            .asSequence()
            .flatMap { it.iterator().asSequence() }
            .filter { it.properties["animationId"] is Int }
            .associateByTo(stateTiles) { it.properties["animationId"] as Int }
        val entityObjects = map.getLayer(data.tmx.entitiesLayerName)?.objects
        val entities = if (entityObjects != null) convertToEntitiesAndEnhanceLayerObjects(map, entityObjects) else emptyList()
        return LevelImpl(data.name, map, Entities(entities))
    }

    /**
     * Converts layer objects to an enhanced one which includes Entity objects and returns those Entity objects
     * @see io.github.hider.tilegame.map.TiledMapRenderer.renderObject
     */
    private fun convertToEntitiesAndEnhanceLayerObjects(map: TiledGameMap, entityObjects: MapObjects): List<Entity> {
        entityObjects.forEachIndexed { index, obj ->
            if (obj is TiledMapTileMapObject) {
                val entity = createEntity(obj, map)
                entityObjects[index] = TiledMapEntityObject(entity, obj)
            }
        }

        entityObjects.sort { a, b ->
            // Make sure Player is always rendered last
            if (a is TiledMapEntityObject && a.entity is Player) 1
            else if (b is TiledMapEntityObject && b.entity is Player) -1
            else 0
        }

        return entityObjects.filterIsInstance<TiledMapEntityObject>().map { it.entity }
    }

    private fun createEntity(obj: TiledMapTileMapObject, map: TiledGameMap): Entity {
        return when (obj.tile.properties["type"] as String?) {
            Player::class.qualifiedName -> Player(createProps(obj), this, map)
            Spike::class.qualifiedName -> Spike(createProps(obj))
            Collectible::class.qualifiedName -> Collectible(createProps(obj))
            EndProtector::class.qualifiedName -> EndProtector(createProps(obj))
            EndButton::class.qualifiedName -> EndButton(createProps(obj))
            Wirler::class.qualifiedName -> Wirler(createProps(obj), this, map)
            else -> NullEntity(obj.properties["gid"] as Int)
        }
    }

    private fun createProps(obj: TiledMapTileMapObject): EntityProps {
        val width = obj.properties["width"] as Float
        val height = obj.properties["height"] as Float
        return EntityProps(
            obj.properties["id"] as Int,
            Vector2(obj.x, obj.y),
            findStateTextures(obj),
            width,
            height,
            findHitbox(obj) ?: Rectangle(0f, 0f, width, height),
            EntityProps.Flip(obj.isFlipVertically, obj.isFlipHorizontally),
        )
    }

    private fun findStateTextures(obj: TiledMapTileMapObject): EntityProps.EntityStateTexture {
        val walking = obj.tile.properties["walkingAnimationId"] as Int?
        val jumping = obj.tile.properties["jumpingAnimationId"] as Int?
        val down = obj.tile.properties["downAnimationId"] as Int?

        return EntityProps.EntityStateTexture(
            obj.tile.textureRegion,
            { stateTiles[walking]?.textureRegion ?: obj.tile.textureRegion },
            stateTiles[jumping]?.textureRegion ?: obj.tile.textureRegion,
            stateTiles[down]?.textureRegion ?: obj.tile.textureRegion,
        )
    }

    private fun findHitbox(obj: TiledMapTileMapObject): Rectangle? {
        var result: Rectangle? = null
        obj.tile.objects.filterIsInstance<RectangleMapObject>().forEach {
            if (result == null && it.name == "hitbox") result = it.rectangle
            else {
                log.warn("Unknown object in tile {} (entity #{})", obj.tile.properties["type"], obj.properties["id"])
            }
        }
        return result
    }
}

private class LevelImpl(
    override val name: String,
    override val map: TiledGameMap,
    override val entities: Entities,
) : Level {

    private val subscribers = mutableMapOf<KClass<out LevelEvent>, MutableList<Consumer<LevelEvent>>>()

    override fun dispatchEvent(event: LevelEvent) {
        subscribers[event::class]?.forEach { it.accept(event) }
    }

    override fun <T : LevelEvent> subscribeEvent(event: KClass<out T>, action: Consumer<T>) {
        @Suppress("UNCHECKED_CAST")
        subscribers.computeIfAbsent(event) { mutableListOf() }.add(action as Consumer<LevelEvent>)
    }

    override fun dispose() = map.dispose()
}

@Serializable
private data class LevelData(
    val name: String,
    val tmx: TmxConfig
)
