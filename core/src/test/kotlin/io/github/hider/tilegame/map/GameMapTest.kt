package io.github.hider.tilegame.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals


internal class GameMapTest {

    @Test
    fun `map collision with rectangle`() {
        val map = object : GameMap() {
            override val width = 10
            override val height = 10
            override val layerCount = 1
            override val blocksLayerIndex = 1
            override val minViewSizeWidthToHeight = 1f to 1f
            override val tileSize = 2

            override fun render(camera: OrthographicCamera) {}
            override fun getTileTypeByCoordinate(layer: Int, col: Int, row: Int) = TileType(1, "test", true)
            override fun dispose() {}
        }
        val result = map.findCollidedTilesWithMapByRect(Rectangle(1f, 1f, 1f, 1f))
        assertEquals(1, result.size)
        assertEquals(Rectangle(0f, 0f, 2f, 2f), result[0])
    }
}
