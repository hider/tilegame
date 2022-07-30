package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.levels.Level
import io.github.hider.tilegame.levels.LevelEvent
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.map.createMap
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
internal class EntityTest {

    @Test
    fun `entity props`() {
        val texture = mockk<TextureRegion>()
        val stateTexture = EntityProps.EntityStateTexture(texture, { texture }, texture, texture)
        val pos = Vector2(2f, 4f)
        val props = EntityProps(1, pos, stateTexture, 10f, 20f, null, EntityProps.Flip(false, false))
        val levelLoader = mockk<LevelLoader>()
        val map = createMap()
        val entity = Player(props, levelLoader, map)
        assertEquals(props.renderWidth, entity.width)
        assertEquals(props.renderHeight, entity.height)
        assertEquals(props.position, entity.position)
        assertEquals(Rectangle(pos.x, pos.y, 10f, 20f), entity.toRectangle())
    }

    @Test
    fun hitbox() {
        val texture = mockk<TextureRegion>()
        val batch = mockk<Batch>()

        val stateTexture = EntityProps.EntityStateTexture(texture, { texture }, texture, texture)
        val pos = Vector2(2f, 4f)
        var props = EntityProps(1, pos, stateTexture, 10f, 20f, Rectangle(1f, 2f, 3f, 4f), EntityProps.Flip(false, false))
        var entity = Collectible(props)
        assertEquals(Vector2(2f + 1f, 4f + 2f), entity.position)
        assertEquals(props.hitbox!!.height, entity.height)
        assertEquals(props.hitbox!!.width, entity.width)

        justRun { batch.draw(any<TextureRegion>(), pos.x, pos.y, props.renderWidth, props.renderHeight) }
        entity.render(batch)
        verify { batch.draw(any<TextureRegion>(), pos.x, pos.y, props.renderWidth, props.renderHeight) }

        props = EntityProps(1, pos, stateTexture, 10f, 20f, Rectangle(1f, 2f, 3f, 4f), EntityProps.Flip(true, false))
        entity = Collectible(props)
        assertEquals(Vector2(2f + 1f, 4f + props.renderHeight - 2f - 4f), entity.position)

        justRun { batch.draw(any<TextureRegion>(), entity.position.x - 1f, entity.position.y - 2f + 4f, props.renderWidth, -props.renderHeight) }
        entity.render(batch)
        verify { batch.draw(any<TextureRegion>(), entity.position.x - 1f, entity.position.y - 2f + 4f, props.renderWidth, -props.renderHeight) }
    }


    @Test
    fun collision() {
        val texture = mockk<TextureRegion>()
        val levelLoader = mockk<LevelLoader>()

        val stateTexture = EntityProps.EntityStateTexture(texture, { texture }, texture, texture)
        val pos = Vector2(2f, 4f)
        val props = EntityProps(1, pos, stateTexture, 10f, 20f, Rectangle(1f, 2f, 3f, 4f), EntityProps.Flip(false, false))
        val map = createMap()
        val level = object : Level {
            override val name = "level name"
            override val map = map
            override val entities = Entities(emptyList())
            override fun dispatchEvent(event: LevelEvent) {}
            override fun <T : LevelEvent> subscribeEvent(event: KClass<out T>, action: Consumer<T>) {}
            override fun dispose() {}
        }
        val entity = Wirler(props, levelLoader, map)

        every { levelLoader.currentLevel } returns level
        entity.update(10f)
        verify { levelLoader.currentLevel }

        assertEquals(Vector2(4f,8f), entity.position)
    }
}
