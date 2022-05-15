package io.github.hider.tilegame

import com.badlogic.gdx.Gdx

class FatalGameException(message: String, cause: Throwable) : RuntimeException(message, cause) {
    init {
        Gdx.app.exit()
    }
}
