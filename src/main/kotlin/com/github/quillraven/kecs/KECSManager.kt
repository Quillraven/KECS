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

    /**
     * Adds a [KECSEntity] to the manager and initializes the entity according to the [init] block.
     * The entity is retrieved from a [Pool] out of the [KECSManager].
     */
    fun entity(init: KECSEntity.() -> Unit = {}): KECSEntity {
        val entity = entityPool.obtain().apply(init)
        entities.add(entity)
        return entity
    }

    /**
     * Removes an existing [KECSEntity] of the manager.
     * Returns the entity back to a [Pool] of the [KECSManager].
     */
    fun freeEntity(entity: KECSEntity) {
        entityPool.free(entity)
        entities.removeValue(entity, true)
    }

    /**
     * Obtains a new instance of a [KECSComponent] from a related [Pool].
     * This function should not be called directly. Use [KECSEntity.add] instead.
     */
    fun <T : KECSComponent> component(type: KClass<T>): T = componentPools[type].obtain()

    /**
     * Removes an existing [KECSComponent] of the manager and returns it back to a [Pool].
     * This function should not be called directly. Use [KECSEntity.remove] instead.
     */
    fun <T : KECSComponent> freeComponent(component: T) = componentPools.free(component)

    /**
     * Returns the amount of free [KECSComponent] of the [Pool] of type [type].
     */
    fun <T : KECSComponent> freeComponentSize(type: KClass<T>) = componentPools[type].free
}
