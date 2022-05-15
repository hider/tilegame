package io.github.hider.tilegame.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import io.github.hider.tilegame.TileGame

open class ButtonInputProcessor(private val game: TileGame) : InputAdapter() {
    private var altKeyDown = false

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.ALT_LEFT) {
            altKeyDown = true
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int) = when (keycode) {
        Keys.ALT_LEFT -> {
            altKeyDown = false
            true
        }
        Keys.Q -> {
            Gdx.app.exit()
            true
        }
        Keys.M -> {
            game.showMainMenu()
            true
        }
        Keys.F5 -> {
            game.resetLevel()
            true
        }
        Keys.ENTER -> {
            if (altKeyDown) game.toggleFullscreen() else false
        }
        Keys.F10 -> {
            game.enableFreeCamera = !game.enableFreeCamera
            true
        }
        Keys.F12 -> {
            game.showDebugInfo = !game.showDebugInfo
            true
        }
        else -> false
    }
}
