package io.github.hider.tilegame.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.utils.Disposable


class Fonts : Disposable {

    // as of 1.11.0 libGDX's default font no longer works, same issue: https://github.com/libgdx/libgdx/issues/6933
    val default = BitmapFont(Gdx.files.internal("ui/default.fnt"), Gdx.files.internal("ui/default.png"), false)
    // region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    val sansSerif22 : BitmapFont
    val sansSerif72 : BitmapFont

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/Sansation-Regular.ttf"))
        val parameter = FreeTypeFontParameter().apply {
            size = 22
            shadowColor = Color.BLACK
            shadowOffsetX = 1
            shadowOffsetY = 1
        }
        sansSerif22 = generator.generateFont(parameter) // font size 12 pixels

        parameter.size = 72
        sansSerif72 = generator.generateFont(parameter)

        generator.dispose()
    }

    override fun dispose() {
        sansSerif22.dispose()
        sansSerif72.dispose()
        default.dispose()
    }
}
