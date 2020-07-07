package com.github.quillraven.kecs.system

import com.github.quillraven.kecs.EntityListener
import com.github.quillraven.kecs.IteratingSystem
import com.github.quillraven.kecs.World

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

class TestIteratingSystem(world: World) : IteratingSystem(
    world.family { }
) {
    var updateCalls = 0
    var updateEntityCalls = 0

    override fun update(world: World, deltaTime: Float) {
        super.update(world, deltaTime)
        updateCalls++
    }

    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        updateEntityCalls++
    }
}