package io.github.hider.tilegame

import com.badlogic.gdx.math.Rectangle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TileGame {

    @Test
    fun `rectangle intersection extension`() {
        val rectA = Rectangle(1f, 2f, 3f, 4f)
        val rectB = Rectangle(0f, 0f, 10f, 10f)
        val result = rectA.intersection(rectB)
        assertEquals(Rectangle(1f, 2f, 3f, 4f), result)
    }

    @Test
    fun `rectangle intersection extension returns null`() {
        val rectA = Rectangle(1f, 2f, 3f, 4f)
        val rectB = Rectangle(5f, 6f, 7f, 8f)
        val result = rectA.intersection(rectB)
        assertNull(result)
    }
}
