package io.github.hider.tilegame.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.map.GameMap
import io.github.hider.tilegame.map.TileType
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EntityTest {

    @Test
    fun `entity props`() {
        val texture = mockk<TextureRegion>()
        val stateTexture = EntityProps.EntityStateTexture(texture, { texture }, texture, texture)
        val props = EntityProps(1, Vector2(2f, 4f), stateTexture, 10f, 20f, null, EntityProps.Flip(false, false))
        val levelLoader = mockk<LevelLoader>()
        val map = object : GameMap() {
            override val width = 10
            override val height = 10
            override val layerCount = 1
            override val blocksLayerIndex = 1
            override val minViewSizeWidthToHeight = 1f to 1f
            override val tileSize = 2
            override val backgroundColor = Color.BLUE

            override fun render(camera: OrthographicCamera) {}
            override fun getTileTypeByCoordinate(layer: Int, col: Int, row: Int) = TileType(1, "test", true)
            override fun dispose() {}
        }
        val entity = Player(props, levelLoader, map)
        assertEquals(entity.width, props.renderWidth)
        assertEquals(entity.height, props.renderHeight)
        assertEquals(entity.position, props.position)
    }
}
