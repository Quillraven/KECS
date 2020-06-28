package com.github.quillraven.kecs.system

import com.github.quillraven.kecs.KECSInterpolationSystem

class TestInterpolationSystem : KECSInterpolationSystem(1f) {
    var intervalCalls = 0
    var lastInterval = 0f
    var interpolateCalls = 0
    var lastAlpha = 0f

    override fun interval(interval: Float) {
        lastInterval = interval
        ++intervalCalls
    }

    override fun interpolate(alpha: Float) {
        lastAlpha = alpha
        ++interpolateCalls
    }
}
