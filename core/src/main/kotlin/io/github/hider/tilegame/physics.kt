package io.github.hider.tilegame

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

val GRAVITY = Vector2(0f, -9.81f)

operator fun Vector2.plus(other: Vector2): Vector2 = Vector2(this).add(other)
operator fun Vector2.times(scalar: Int): Vector2 = Vector2(this).scl(scalar.toFloat())
operator fun Vector2.times(scalar: Float): Vector2 = Vector2(this).scl(scalar)

operator fun Vector2.plusAssign(other: Vector2) {
    add(other)
}

operator fun Vector2.timesAssign(scalar: Float) {
    scl(scalar)
}

fun Rectangle.intersection(other: Rectangle): Rectangle? {
    val intersect = Rectangle()
    if (Intersector.intersectRectangles(this, other, intersect)) {
        return intersect
    }
    return null
}
