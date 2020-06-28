package com.github.quillraven.kecs.system

import com.github.quillraven.kecs.KECSIntervalSystem

class TestIntervalSystem : KECSIntervalSystem(1f) {
    var calls = 0
    var lastInterval = 0f

    override fun interval(interval: Float) {
        lastInterval = interval
        ++calls
    }
}
