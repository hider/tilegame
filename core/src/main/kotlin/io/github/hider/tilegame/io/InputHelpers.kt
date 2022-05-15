package io.github.hider.tilegame.io

import com.badlogic.gdx.Gdx

object InputHelpers {

    fun isPressed(vararg keys: Int) = keys.any { key -> Gdx.input.isKeyPressed(key) }
}
