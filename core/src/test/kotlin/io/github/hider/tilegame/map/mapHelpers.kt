package io.github.hider.tilegame.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera

fun createMap() = object : GameMap() {
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
