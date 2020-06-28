package com.github.quillraven.kecs.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.kecs.KECSComponent

class TransformComponent : KECSComponent, Comparable<TransformComponent> {
    val position = Vector2()

    override fun reset() {
        position.set(0f, 0f)
    }

    override fun compareTo(other: TransformComponent) = position.y.compareTo(other.position.y)
}
