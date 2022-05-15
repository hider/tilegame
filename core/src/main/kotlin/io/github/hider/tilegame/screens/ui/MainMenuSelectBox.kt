package io.github.hider.tilegame.screens.ui

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin

data class SelectBoxItem<T>(val label: String, val item: T)

class MainMenuSelectBox<T>(skin: Skin) : SelectBox<SelectBoxItem<T>>(skin) {
    override fun toString(item: SelectBoxItem<T>) = item.label
}
