package io.github.hider.tilegame.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable

class Sound : Disposable {

    private val collectSfx = Gdx.audio.newSound(Gdx.files.internal("levels/mixkit-player-jumping-in-a-video-game-2043.wav"))

    fun play(sfx: Sfx) {
        when (sfx) {
            Sfx.Collect -> collectSfx.play()
        }
    }

    override fun dispose() {
        collectSfx.dispose()
    }

    enum class Sfx {
        Collect
    }
}
