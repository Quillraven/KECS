package com.github.quillraven.kecs.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.kecs.KECSComponent

class TransformComponent : KECSComponent {
    val position = Vector2()

    override fun reset() {
        position.set(0f, 0f)
    }
}
