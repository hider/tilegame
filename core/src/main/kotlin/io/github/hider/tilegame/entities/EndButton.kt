package io.github.hider.tilegame.entities

class EndButton(initProps: EntityProps): EntityWithHitbox(initProps) {

    var down = false

    override fun update(deltaTime: Float) {
        textureRegion = if (down) {
            initProps.stateTexture.down
        } else {
            initProps.stateTexture.idle
        }
    }
}
