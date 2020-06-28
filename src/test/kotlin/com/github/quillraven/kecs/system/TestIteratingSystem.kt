package com.github.quillraven.kecs.system

import com.github.quillraven.kecs.KECSEntity
import com.github.quillraven.kecs.KECSIteratingSystem
import com.github.quillraven.kecs.KECSManager
import com.github.quillraven.kecs.component.PlayerComponent

class TestIteratingSystem(manager: KECSManager) :
    KECSIteratingSystem(manager.family { allOf(PlayerComponent::class) }) {
    var updateCalls = 0
    var updateEntityCalls = 0

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        updateCalls++
    }

    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        ++updateEntityCalls
        entity[PlayerComponent::class].updateCalls++
    }
}
