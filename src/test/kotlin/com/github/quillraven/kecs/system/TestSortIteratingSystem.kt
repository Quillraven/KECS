package com.github.quillraven.kecs.system

import com.github.quillraven.kecs.KECSEntity
import com.github.quillraven.kecs.KECSIteratingSystem
import com.github.quillraven.kecs.KECSManager
import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.TransformComponent

class TestSortIteratingSystem(manager: KECSManager) :
    KECSIteratingSystem(manager.family { allOf(PlayerComponent::class, TransformComponent::class) }) {
    private val comparator = compareBy<KECSEntity> { it[TransformComponent::class] }
    var updateCalls = 0
    var updateEntityCalls = 0

    override fun update(deltaTime: Float) {
        sort(comparator)
        super.update(deltaTime)
        updateCalls++
    }

    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[TransformComponent::class].position.x = updateEntityCalls.toFloat()
        ++updateEntityCalls
    }
}
