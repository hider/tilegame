package io.github.hider.tilegame.screens.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.hider.tilegame.entities.EntityWithHitbox

class CollisionShapeRenderer(private val collidables: Collection<EntityWithHitbox>) {

    private val renderer = ShapeRenderer().apply {
        color = Color(Color.BLUE).apply {
            a = .3f
        }
    }

    fun render(camera: Camera) {
        val blendOriginal = Gdx.gl.glIsEnabled(GL20.GL_BLEND)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        renderer.projectionMatrix = camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        collidables.forEach {
            renderer.rect(it.position.x, it.position.y, it.width, it.height)
        }
        renderer.end()
        if (!blendOriginal) {
            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
    }
}
