package io.github.hider.tilegame.levels

import com.badlogic.gdx.utils.Disposable
import io.github.hider.tilegame.entities.Entities
import io.github.hider.tilegame.map.GameMap
import java.util.function.Consumer
import kotlin.reflect.KClass

interface Level : Disposable {
    val name: String
    val map: GameMap
    val entities: Entities
    fun dispatchEvent(event: LevelEvent)
    fun <T : LevelEvent> subscribeEvent(event: KClass<out T>, action: Consumer<T>)
}
