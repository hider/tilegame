package io.github.hider.tilegame.map

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import io.github.hider.tilegame.entities.Entity

class TiledMapEntityObject(val entity: Entity, obj: TiledMapTileMapObject) : TiledMapTileMapObject(obj.tile, obj.isFlipHorizontally, obj.isFlipVertically)
