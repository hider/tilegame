package io.github.hider.tilegame.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import io.github.hider.tilegame.intersection

abstract class GameMap : Disposable {

    abstract fun render(camera: OrthographicCamera)

    abstract val width: Int
    abstract val height: Int
    abstract val layerCount: Int
    abstract val blocksLayerIndex: Int
    abstract val minViewSizeWidthToHeight: Pair<Float?, Float?>

    /**
     * Tile size in pixels
     */
    abstract val tileSize: Int

    abstract fun getTileTypeByCoordinate(layer: Int, col: Int, row: Int): TileType?

    fun getTileTypeByLocation(layer: Int, x: Float, y: Float): TileType? {
        return getTileTypeByCoordinate(layer, getTileColumnByX(x), getTileRowByY(y))
    }

    fun findCollidedTilesWithMapByRect(rect: Rectangle): List<Rectangle> {
        val result = mutableListOf<Rectangle>()
        var row = getTileRowByY(rect.y)
        val rowLast = getTileRowByY(rect.y + rect.height)
        val colLast = getTileColumnByX(rect.x + rect.width)
        while (row <= rowLast) {
            var col = getTileColumnByX(rect.x)
            while (col <= colLast) {
                val tile = getTileTypeByCoordinate(blocksLayerIndex, col, row)
                if (tile != null && tile.collidable) {
                    val tileRect = Rectangle(
                        (col * tileSize).toFloat(),
                        (row * tileSize).toFloat(),
                        tileSize.toFloat(),
                        tileSize.toFloat(),
                    )
                    rect.intersection(tileRect)?.let {
                        result.add(tileRect)
                    }
                }
                ++col
            }
            ++row
        }
        return result
    }

    fun getTilePieceByLocation(layer: Int, x: Float, y: Float): TilePiece? {
        val col = getTileColumnByX(x)
        val row = getTileRowByY(y)
        val tile = getTileTypeByCoordinate(layer, col, row)
        if (tile != null) {
            return TilePiece(col, row, tile)
        }
        return null
    }

    fun getPixelWidth() = width * tileSize
    fun getPixelHeight() = height * tileSize
    private fun getTileColumnByX(x: Float) = (x / tileSize).toInt()
    private fun getTileRowByY(y: Float) = (y / tileSize).toInt()
}
