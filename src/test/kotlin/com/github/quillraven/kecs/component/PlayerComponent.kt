package com.github.quillraven.kecs.component

import com.github.quillraven.kecs.KECSComponent

class PlayerComponent : KECSComponent {
    var updateCalls = 0

    override fun reset() {
        updateCalls = 0
    }
}
