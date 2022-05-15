package io.github.hider.tilegame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScalingViewport
import io.github.hider.tilegame.TileGame
import io.github.hider.tilegame.screens.ui.MainMenuSelectBox
import io.github.hider.tilegame.screens.ui.SelectBoxItem


class MainMenuScreen(private val game: TileGame) : ScreenAdapter(), Disposable {

    private val viewport = ScalingViewport(
        Scaling.none,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat(),
        OrthographicCamera().apply {
            setToOrtho(false)
        },
    )
    private val stage = Stage(
        viewport,
        game.batch
    )
    private val table = Table().apply {
        setFillParent(true)
    }

    private var selectedLevel: String? = null

    init {
        stage.addListener(object : InputListener() {
            override fun keyDown(event: InputEvent?, keycode: Int) = game.inputProcessor.keyDown(keycode)
            override fun keyUp(event: InputEvent?, keycode: Int) = game.inputProcessor.keyUp(keycode)
        })

        val text1 = Label("Welcome to Tile Game!", game.skin)
        table.add(text1).colspan(2).padBottom(Gdx.graphics.height * .15f)

        table.row()
        val levelSelectorLabel = Label("Level:", game.skin)
        table.add(levelSelectorLabel).left().padBottom(Gdx.graphics.height * .01f)

        val levelSelector = MainMenuSelectBox<String>(game.skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent, actor: Actor) {
                    selectedLevel = selected.item
                }
            })
            setItems(Array(game.levelLoader.levels().map { SelectBoxItem(it, it) }.toTypedArray()))
        }
        table.add(levelSelector).fillX().padBottom(Gdx.graphics.height * .01f)

        table.row()
        val startButton = TextButton("Load level", game.skin).apply {
            pad(10f, 20f, 10f, 20f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    val lvl = selectedLevel
                    if (lvl != null) {
                        game.showGameScreen(lvl)
                    }
                }
            })
        }
        table.add(startButton).colspan(2).fillX().padBottom(Gdx.graphics.height * .01f)

        table.row()
        val quitButton = TextButton("Quit", game.skin).apply {
            pad(10f, 20f, 10f, 20f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    Gdx.app.exit()
                }
            })
        }
        table.add(quitButton).colspan(2).fillX()

        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.ROYAL)
        table.debug = game.showDebugInfo
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}
