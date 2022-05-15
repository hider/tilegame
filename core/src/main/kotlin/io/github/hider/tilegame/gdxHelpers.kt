package io.github.hider.tilegame

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.utils.Array
import java.util.Comparator

fun Batch.use(commands: () -> Unit) {
    begin()
    commands()
    end()
}

private fun accessObjectsField(mapObjects: MapObjects): Array<MapObject> {
    val field = MapObjects::class.java.getDeclaredField("objects")
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(mapObjects) as Array<MapObject>
}

operator fun MapObjects.set(index: Int, value: MapObject) {
    val objects = accessObjectsField(this)
    objects[index] = value
}

fun MapObjects.sort(comparator: Comparator<MapObject>) {
    val objects = accessObjectsField(this)
    objects.sort(comparator)
}
