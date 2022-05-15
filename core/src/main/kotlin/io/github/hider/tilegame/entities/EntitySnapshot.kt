package io.github.hider.tilegame.entities

@kotlinx.serialization.Serializable
data class EntitySnapshot(
    val type: String,
    val x: Float,
    val y: Float,
)
