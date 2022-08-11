package io.github.hider.tilegame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import io.github.hider.tilegame.io.ButtonInputProcessor
import io.github.hider.tilegame.io.Fonts
import io.github.hider.tilegame.io.Sound
import io.github.hider.tilegame.levels.LevelLoader
import io.github.hider.tilegame.screens.GameScreen
import io.github.hider.tilegame.screens.MainMenuScreen
import org.slf4j.LoggerFactory

class TileGame : Game() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    lateinit var batch: SpriteBatch
        private set
    lateinit var fonts: Fonts
        private set
    lateinit var skin: Skin
        private set
    lateinit var sound: Sound
        private set
    lateinit var levelLoader: LevelLoader
        private set
    val inputProcessor = ButtonInputProcessor(this)

    private val disposables: MutableSet<Disposable> = mutableSetOf()
    private var previousWidthAndHeight = 0 to 0
    private var mainMenuScreen: Screen? = null
    private var gameScreen: Screen? = null

    var showDebugInfo = false
    var enableFreeCamera = false
    var showCollisionShapes = false

    override fun create() {
        batch = SpriteBatch()
        fonts = Fonts()
        skin = Skin(Gdx.files.internal("ui/uiskin.json"))
        sound = Sound()
        levelLoader = LevelLoader("levels.json")

        showMainMenu()
    }

    override fun dispose() {
        super.dispose()
        gameScreen?.dispose()
        mainMenuScreen?.dispose()
        sound.dispose()
        levelLoader.dispose()
        skin.dispose()
        fonts.dispose()
        batch.dispose()
        disposables.forEach(Disposable::dispose)
    }

    fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun showMainMenu() {
        if (mainMenuScreen == null) {
            mainMenuScreen = MainMenuScreen(this)
        }
        if (screen !== mainMenuScreen) {
            setScreen(mainMenuScreen)
        }
    }

    fun showGameScreen(levelName: String) {
        if (levelName != levelLoader.currentLevel?.name) {
            val level = try {
                levelLoader.load(levelName, batch)
            } catch (e: Exception) {
                val message = "Failed to load level '$levelName'. Exiting..."
                log.error(message, e)
                throw FatalGameException(message, e)
            }
            gameScreen?.dispose()
            gameScreen = GameScreen(this, level)
        }
        Gdx.input.inputProcessor = inputProcessor
        setScreen(gameScreen)
    }

    fun toggleFullscreen(): Boolean {
        return if (Gdx.graphics.isFullscreen) {
            Gdx.graphics.setWindowedMode(previousWidthAndHeight.first, previousWidthAndHeight.second)
        } else {
            previousWidthAndHeight = Gdx.graphics.width to Gdx.graphics.height
            Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        }
    }

    fun resetLevel() {
        val levelName = levelLoader.currentLevel?.name
        if (levelName != null) {
            levelLoader.reset()
            showGameScreen(levelName)
        }
    }
}
