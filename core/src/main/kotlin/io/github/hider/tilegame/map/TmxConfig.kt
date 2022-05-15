package io.github.hider.tilegame.map

import kotlinx.serialization.Serializable

@Serializable
data class TmxConfig(
    val fileName: String,
    val tileSetName: String,
    val blocksLayerName: String = "Blocks",
    val entitiesLayerName: String = "Entities",
)
