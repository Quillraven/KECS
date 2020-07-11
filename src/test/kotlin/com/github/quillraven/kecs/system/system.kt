package com.github.quillraven.kecs.system

import com.github.quillraven.kecs.ComponentManager
import com.github.quillraven.kecs.EntityListener
import com.github.quillraven.kecs.IteratingSystem
import com.github.quillraven.kecs.World
import com.github.quillraven.kecs.component.TransformComponent

class TestEntityListener : EntityListener {
    var addCalls = 0
    var removeCalls = 0

    override fun entityAdded(entityID: Int) {
        addCalls++
    }

    override fun entityRemoved(entityID: Int) {
        removeCalls++
    }
}

class TestIteratingSystem(
    world: World,
    private val manager: ComponentManager<TransformComponent> = world.componentManager()
) : IteratingSystem(
    world.family { allOf(TransformComponent::class) }
) {
    var updateCalls = 0
    var updateEntityCalls = 0
    var lastUpdateDeltaTime = 0f
    var lastUpdateEntityDeltaTime = 0f

    override fun update(world: World, deltaTime: Float) {
        super.update(world, deltaTime)
        updateCalls++
        lastUpdateDeltaTime = deltaTime
    }

    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        updateEntityCalls++
        lastUpdateEntityDeltaTime = deltaTime
        manager[entityID].x = updateEntityCalls
    }
}