package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import kotlin.reflect.KClass

class KECSManager(initialEntitySize: Int = 16) {
    private val entities = Array<KECSEntity>(initialEntitySize)
    val entitySize: Int get() = entities.size
    private val entityPool = object : Pool<KECSEntity>() {
        override fun newObject(): KECSEntity = KECSEntity(this@KECSManager)
    }
    val freeEntitySize: Int get() = entityPool.free

    private val componentPools = ComponentPools()

    fun entity(init: KECSEntity.() -> Unit = {}): KECSEntity {
        val entity = entityPool.obtain().apply(init)
        entities.add(entity)
        return entity
    }

    fun freeEntity(entity: KECSEntity) {
        entityPool.free(entity)
        entities.removeValue(entity, true)
    }

    fun <T : KECSComponent> component(type: KClass<T>): T = componentPools[type].obtain()

    fun <T : KECSComponent> freeComponent(component: T) = componentPools.free(component)

    fun <T : KECSComponent> freeComponentSize(type: KClass<T>) = componentPools[type].free
}
