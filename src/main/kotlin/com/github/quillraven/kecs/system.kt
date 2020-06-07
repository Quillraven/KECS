package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array

interface KECSSystem {
    fun update(deltaTime: Float)
}

abstract class KECSIntervalSystem(private val rate: Float) : KECSSystem {
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= rate) {
            accumulator -= rate
            interval(rate)
        }
    }

    abstract fun interval(interval: Float)
}

abstract class KECSInterpolationSystem(private val rate: Float) : KECSSystem {
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= rate) {
            accumulator -= rate
            interval(rate)
        }

        interpolate(accumulator / rate)
    }

    abstract fun interpolate(alpha: Float)

    abstract fun interval(interval: Float)
}

class KECSSystemManager(vararg systems: KECSSystem) {
    val systems = Array<KECSSystem>(false, systems.size).apply {
        systems.forEach { add(it) }
    }

    fun update(deltaTime: Float) = systems.forEach { it.update(deltaTime) }
}
