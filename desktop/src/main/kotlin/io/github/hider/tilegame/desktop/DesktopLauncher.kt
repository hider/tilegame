package io.github.hider.tilegame.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation.GL30
import io.github.hider.tilegame.FatalGameException
import io.github.hider.tilegame.TileGame
import javax.swing.JOptionPane
import javax.swing.UIManager

private const val GAME_TITLE = "Tile Game"

fun main() {
    try {
        Lwjgl3Application(TileGame(), Lwjgl3ApplicationConfiguration().apply {
            setTitle(GAME_TITLE)
            setWindowedMode(1280, 720)
            setResizable(true)
            setOpenGLEmulation(GL30, 4, 2)
        })
    } catch (e: FatalGameException) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        JOptionPane.showMessageDialog(null, e.message, GAME_TITLE, JOptionPane.ERROR_MESSAGE)
    }
}
