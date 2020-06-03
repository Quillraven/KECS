package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import kotlin.reflect.KClass

class KECSManager(
    initialEntityCapacity: Int = 100,
    initialComponentCapacity: Int = 20
) {
    private val entityManager = KECSEntityManager(this, initialEntityCapacity)
    private val componentManager = KECSComponentManager(initialEntityCapacity, initialComponentCapacity)

    fun entity(init: KECSEntity.() -> Unit = {}) = entityManager.obtain().apply { init() }

    operator fun contains(entity: KECSEntity) = entity in entityManager

    inline fun <reified T : KECSComponent> componentFor(entity: KECSEntity): T = componentFor(entity, T::class) as T

    fun <T : KECSComponent> componentFor(entity: KECSEntity, type: KClass<T>) =
        componentManager.obtain(type).apply { componentManager.add(entity, this) }

    fun componentsOf(entity: KECSEntity): Array<KECSComponent> = componentManager.entityComponents[entity.id]

    fun mapper(type: KClass<out KECSComponent>) = componentManager.mapper(type)
}
