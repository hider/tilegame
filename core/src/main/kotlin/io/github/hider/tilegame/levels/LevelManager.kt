package io.github.hider.tilegame.levels

import io.github.hider.tilegame.TileGame
import io.github.hider.tilegame.entities.Collectible
import io.github.hider.tilegame.entities.EndButton
import io.github.hider.tilegame.entities.EndProtector
import io.github.hider.tilegame.screens.ui.Hud
import io.github.hider.tilegame.screens.ui.UiMessages

class LevelManager(level: Level, private val hud: Hud, private val game: TileGame) {

    private val collectibleCount = level.entities.collidables.filterIsInstance<Collectible>().size
    private val endProtector = level.entities.collidables.filterIsInstance<EndProtector>().firstOrNull()
    private val endButton = level.entities.collidables.filterIsInstance<EndButton>().firstOrNull()
    private val levelNames = game.levelLoader.levels()
    private val nextLevelIndex = levelNames.indexOf(level.name) + 1

    private var collectedCount = 0
    private var levelEnd = false
    private var resetLevelAtMillis = 0L
    private var forwardLevelAtMillis = 0L

    init {
        level.subscribeEvent(CollectedEvent::class) {
            ++collectedCount
        }
        level.subscribeEvent(PlayerDiedEvent::class) {
            resetLevelAtMillis = System.currentTimeMillis() + 3000L
        }
        level.subscribeEvent(LevelEndEvent::class) {
            levelEnd = true
        }
    }

    fun update() {
        if (collectibleCount > 0 && endButton != null) {
            if (collectedCount >= collectibleCount && endProtector != null) {
                endProtector.canCollide = false
            }
            if (levelEnd) {
                if (forwardLevelAtMillis == 0L) {
                    hud.addMessage("You won!", game.fonts.sansSerif72, UiMessages.UiMessageLocation.Center)
                    forwardLevelAtMillis = System.currentTimeMillis() + 3000
                } else if (forwardLevelAtMillis < System.currentTimeMillis()) {
                    if (levelNames.size > nextLevelIndex) {
                        game.showGameScreen(levelNames[nextLevelIndex])
                    } else {
                        game.showMainMenu()
                    }
                    levelEnd = false
                }
            }
        }

        if (resetLevelAtMillis != 0L && resetLevelAtMillis <= System.currentTimeMillis()) {
            game.resetLevel()
        }
    }
}
