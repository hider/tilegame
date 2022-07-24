package io.github.hider.tilegame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.hider.tilegame.io.InputHelpers.isPressed
import io.github.hider.tilegame.io.Sound
import io.github.hider.tilegame.TileGame
import io.github.hider.tilegame.levels.*
import io.github.hider.tilegame.screens.ui.DebugText
import io.github.hider.tilegame.screens.ui.Hud
import io.github.hider.tilegame.map.TilePiece
import io.github.hider.tilegame.use


class GameScreen(private val game: TileGame, private val level: Level) : Disposable, ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val mainViewport = ExtendViewport(level.map.minViewSizeWidthToHeight.first ?: level.map.getPixelWidth().toFloat(), level.map.minViewSizeWidthToHeight.second ?: level.map.getPixelHeight().toFloat(), camera)
    private val hudViewport = ScreenViewport()
    private val hud = Hud(hudViewport.camera, game.fonts, level)
    private val levelManager = LevelManager(level, hud, game.fonts)
    private val sounds = mutableListOf<Sound.Sfx>()

    private var touchedTileType: TilePiece? = null
    private var resetLevelAtMillis = 0L

    init {
        level.subscribeEvent(CollectedEvent::class) {
            sounds.add(Sound.Sfx.Collect)
        }
        level.subscribeEvent(PlayerDiedEvent::class) {
            resetLevelAtMillis = System.currentTimeMillis() + 3000L
        }
    }

    override fun render(deltaTime: Float) {
        if (Gdx.graphics.width == 0 || Gdx.graphics.height == 0) {
            // Do not render when screen is not visible, otherwise some division by zero can happen
            return
        }
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        assert(camera === mainViewport.camera)
        if (game.enableFreeCamera && Gdx.input.isTouched && (Gdx.input.deltaX != 0 || Gdx.input.deltaY != 0)) {
            moveCameraByInput()
        } else if (!game.enableFreeCamera) {
            moveCameraToPlayer()
        }
        if (isPressed(Input.Keys.NUMPAD_ADD)) {
            camera.zoom -= deltaTime
        } else if (isPressed(Input.Keys.NUMPAD_SUBTRACT)) {
            camera.zoom += deltaTime
            if (camera.zoom > 1.0f) camera.zoom = 1.0f
        }

        mainViewport.apply()
        // Projection matrix applied by map render
        level.map.render(camera)

        hudViewport.apply()
        hud.render(game.batch)

        if (game.showDebugInfo) {
            renderDebugText()
        }

        if (game.showDebugInfo && Gdx.input.justTouched()) {
            val touchPos = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touchPos)
            touchedTileType = level.map.getTilePieceByLocation(level.map.blocksLayerIndex, touchPos.x, touchPos.y)
        }
        level.entities.update(deltaTime)
        hud.update()
        levelManager.update()
        sounds.forEach { game.sound.play(it) }
        sounds.clear()
        if (resetLevelAtMillis != 0L && resetLevelAtMillis <= System.currentTimeMillis()) {
            level.dispatchEvent(ResetLevelEvent)
        }
    }

    override fun resize(width: Int, height: Int) {
        // Minimizing the window sets width/height to zero
        if (width > 0 && height > 0) {
            mainViewport.update(width, height)
            hudViewport.update(width, height)
            moveCameraToPlayer()
        }
    }

    private fun renderDebugText() {
        val debugText = DebugText(game)
        val tileType = touchedTileType
        if (tileType != null) {
            debugText.add("You clicked on tile: ${tileType.type.name} (${tileType.col},${tileType.row})")
        }
        if (game.enableFreeCamera) {
            debugText.add("Free camera is ON")
        }
        debugText.add("Player pos: ${level.entities.player?.position}")
        val camPos = camera.position
        debugText.add("Cam: $camPos")
        debugText.add("FPS: ${Gdx.graphics.framesPerSecond}")
        game.batch.use {
            debugText.render(hudViewport)
        }
    }

    private fun moveCameraByInput() {
        val deltaX = Gdx.input.deltaX * (mainViewport.worldWidth / mainViewport.screenWidth) * camera.zoom
        val deltaY = Gdx.input.deltaY * (mainViewport.worldHeight / mainViewport.screenHeight) * camera.zoom
        camera.translate(-deltaX, deltaY)
    }

    private fun moveCameraToPlayer() {
        val player = level.entities.player
        if (player != null) {
            moveCameraRespectBounds(player.position)
        }
    }

    private fun moveCameraRespectBounds(newPosition: Vector2) {
        if (newPosition.x - camera.viewportWidth / 2 * camera.zoom < 0) {
            camera.position.x = camera.viewportWidth / 2 * camera.zoom
        } else if (newPosition.x + camera.viewportWidth / 2 * camera.zoom > level.map.getPixelWidth()) {
            camera.position.x = level.map.getPixelWidth() - camera.viewportWidth / 2 * camera.zoom
        } else {
            camera.position.x = newPosition.x
        }
        if (newPosition.y - camera.viewportHeight / 2 * camera.zoom < 0) {
            camera.position.y = camera.viewportHeight / 2 * camera.zoom
        }
        else if (newPosition.y + camera.viewportHeight / 2 * camera.zoom > level.map.getPixelHeight()) {
            if (mainViewport.screenHeight < camera.viewportHeight * (1/camera.zoom)) {
                camera.position.y = level.map.getPixelHeight() - camera.viewportHeight / 2 * camera.zoom
            }
        } else {
            camera.position.y = newPosition.y
        }
    }
}
