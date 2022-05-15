package io.github.hider.tilegame.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader

class TiledGameMap(batch: Batch, private val config: TmxConfig) : GameMap() {

    val tiledMap: TiledMap = TmxMapLoader().load(config.fileName)
    private val tiledMapRenderer = TiledMapRenderer(tiledMap, batch)
    private val tileTypes: List<TileType> = tiledMap
        .tileSets
        .getTileSet(config.tileSetName)
        .filter { it.properties["type"] is String && it.properties["collidable"] is Boolean }
        .map { TileType(it.id, it.properties["type"] as String, it.properties["collidable"] as Boolean) }
    private val baseLayer: TiledMapTileLayer = tiledMap
        .layers
        .find { it is TiledMapTileLayer && it.name == config.blocksLayerName }
        ?.let { it as TiledMapTileLayer }
        ?: throw IllegalArgumentException("TiledMapTileLayer is not found in TMX file")

    private fun getLayer(layer: Int) = tiledMap.layers[layer]

    fun getLayer(layerName: String): MapLayer? = tiledMap.layers[layerName]

    override fun render(camera: OrthographicCamera) {
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
    }

    override fun dispose() {
        tiledMap.dispose()
    }

    override val width = baseLayer.width
    override val height = baseLayer.height
    override val layerCount = tiledMap.layers.count
    override val tileSize = tiledMap.properties["tilewidth"] as Int
    override val blocksLayerIndex = tiledMap.layers.getIndex(baseLayer)
    override val minViewSizeWidthToHeight = tiledMap.properties["minViewSizeWidth"] as Float? to tiledMap.properties["minViewSizeHeight"] as Float?

    override fun getTileTypeByCoordinate(layer: Int, col: Int, row: Int): TileType? {
        val mapLayer = getLayer(layer)
        if (mapLayer is TiledMapTileLayer) {
            val cell = mapLayer.getCell(col, row)
            if (cell?.tile != null) {
                return tileTypes.first { it.id == cell.tile.id }
            }
        }
        return null
    }
}
