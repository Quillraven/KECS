package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.OrderedSet

interface KECSSystem {
    var active: Boolean
    fun update(deltaTime: Float)
}

abstract class KECSIntervalSystem(
    private val rate: Float,
    override var active: Boolean = true
) : KECSSystem {
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

abstract class KECSInterpolationSystem(
    private val rate: Float,
    override var active: Boolean = true
) : KECSSystem {
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

abstract class KECSIteratingSystem(
    family: KECSFamily,
    val entities: OrderedSet<KECSEntity> = family.entities,
    override var active: Boolean = true
) : KECSSystem {
    override fun update(deltaTime: Float) {
        entities.forEach { updateEntity(it, deltaTime) }
    }

    fun sort(comparator: Comparator<KECSEntity>) = entities.orderedItems().sort(comparator)

    abstract fun updateEntity(entity: KECSEntity, deltaTime: Float)
}

class KECSSystemManager(vararg systems: KECSSystem) {
    val systems = Array<KECSSystem>(false, systems.size).apply {
        systems.forEach { add(it) }
    }

    fun update(deltaTime: Float) = systems.forEach {
        if (it.active) {
            it.update(deltaTime)
        }
    }
}
