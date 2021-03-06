package io.github.hider.tilegame.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals


internal class GameMapTest {

    @Test
    fun `map intersection with rectangle`() {
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
        val result = map.findIntersectionWithMapByRect(Rectangle(1f, 1f, 1f, 1f))
        assertEquals(result, Rectangle(1f, 1f, 1f, 1f))
    }
}
