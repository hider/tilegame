package io.github.hider.tilegame.map

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class TiledMapRenderer(map: TiledMap, batch: Batch) : OrthogonalTiledMapRenderer(map, batch) {
    override fun renderObject(o: MapObject) {
        if (o is TiledMapEntityObject) {
            o.entity.render(batch)
        }
        else if (o is TiledMapTileMapObject) {
            val renderWidth = o.properties["width"] as Float * if (o.isFlipHorizontally) -1 else 1
            val renderHeight = o.properties["height"] as Float
            val posX = o.x - if (o.isFlipHorizontally) renderWidth else 0f
            batch.draw(o.tile.textureRegion, posX, o.y, renderWidth, renderHeight)
        }
    }

    override fun renderImageLayer(layer: TiledMapImageLayer) {
        super.renderImageLayer(layer)
        if (layer.properties["repeatX"] == true) {
            // Repeat image if the repeated location is in view
            val repeatX = ((viewBounds.x + viewBounds.width) / imageBounds.width).toInt()
            // Render the in view chunks only
            for (i in repeatX - 1 .. repeatX) {
                // First chunk already rendered by super
                if (i > 0) {
                    batch.draw(layer.textureRegion, imageBounds.x + imageBounds.width * i, imageBounds.y, imageBounds.width, imageBounds.height)
                }
            }
        }
    }
}
