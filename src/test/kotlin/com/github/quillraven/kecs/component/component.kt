package com.github.quillraven.kecs.component

import com.github.quillraven.kecs.ComponentListener
import com.github.quillraven.kecs.ComponentManager

class TransformComponent {
    var x = 0
    var y = 0
}

class PlayerComponent

class TestComponentListener : ComponentListener {
    var addCalls = 0
    var removeCalls = 0
    override fun componentAdded(entityID: Int, manager: ComponentManager<*>) {
        ++addCalls
    }

    override fun componentRemoved(entityID: Int, manager: ComponentManager<*>) {
        ++removeCalls
    }
}
