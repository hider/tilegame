package io.github.hider.tilegame.levels

import io.github.hider.tilegame.io.Fonts
import io.github.hider.tilegame.screens.ui.Hud
import io.github.hider.tilegame.screens.ui.UiMessages

class LevelManager(private val level: Level, private val hud: Hud, private val fonts: Fonts) {

    private val collectibleCount = level.entities.collectibles.size
    private val endProtecter = level.entities.entityBlocks.firstOrNull()
    private val endButton = level.entities.specials.firstOrNull()

    private var collectedCount = 0
    private var levelEnd = false

    init {
        level.subscribeEvent(CollectedEvent::class) {
            ++collectedCount
        }
        level.subscribeEvent(LevelEndEvent::class) {
            levelEnd = true
        }
    }

    fun update() {
        if (collectibleCount > 0 && endButton != null) {
            if (collectedCount >= collectibleCount && endProtecter != null) {
                endProtecter.canCollide = false
            }
            if (levelEnd) {
                hud.addMessage("You won!", fonts.sansSerif72, UiMessages.UiMessageLocation.Center)
                levelEnd = false
                level.dispatchEvent(PlayerDiedEvent(endButton))
            }
        }
    }
}
