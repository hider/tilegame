package io.github.hider.tilegame.map

import com.badlogic.gdx.math.Rectangle
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
internal class GameMapTest {

    @Test
    fun `map collision with rectangle`() {
        val map = createMap()
        val result = map.findCollidedTilesWithMapByRect(Rectangle(1f, 1f, 1f, 1f))
        assertEquals(1, result.size)
        assertEquals(Rectangle(0f, 0f, 2f, 2f), result[0])
    }
}
