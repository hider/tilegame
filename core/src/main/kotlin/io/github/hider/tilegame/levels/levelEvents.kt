package io.github.hider.tilegame.levels

import io.github.hider.tilegame.entities.Entity

sealed class LevelEvent

data class PlayerDiedEvent(
    val cause: Entity
) : LevelEvent()

object CollectedEvent : LevelEvent()

object LevelEndEvent : LevelEvent()
