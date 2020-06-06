package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import java.util.*
import kotlin.math.max

data class KECSEntity(
    val manager: KECSManager,
    val id: Int,
    var active: Boolean
) : Pool.Poolable {
    val componentBits: BitSet
        get() = manager.componentBitsOf(this)

    inline fun <reified T : KECSComponent> add(init: T.() -> (Unit) = {}): T =
        manager.componentFor<T>(this).apply { init() }

    fun free() = manager.free(this)

    // slow version
    operator fun contains(component: KECSComponent) = contains(manager.mapper(component::class))

    // fast version
    operator fun contains(mapper: KECSComponentMapper) =
        manager.componentsOf(this)[mapper.id] != null

    override fun reset() {
        active = false
    }
}

class KECSEntityPool(
    private val manager: KECSManager,
    initialEntityCapacity: Int
) : Pool<KECSEntity>(initialEntityCapacity) {
    var nextId = 0

    override fun newObject(): KECSEntity = KECSEntity(manager, nextId++, true)
}

interface KECSEntityListener {
    fun entityAdded(entity: KECSEntity) = Unit
    fun entityRemoved(entity: KECSEntity) = Unit
    fun entityResize(newSize: Int) = Unit
}

class KECSEntityManager(
    manager: KECSManager,
    initialEntityCapacity: Int
) {
    private val listeners = Array<KECSEntityListener>(false, 16)
    val entityPool = KECSEntityPool(manager, initialEntityCapacity)
    val entities = Array<KECSEntity>(false, initialEntityCapacity).apply {
        // fill array with null values to correctly set the size and to be able to call "set(index,value)"
        repeat(initialEntityCapacity) {
            this.add(null)
        }
    }

    fun obtain(init: KECSEntity.() -> Unit = {}): KECSEntity {
        val entity = entityPool.obtain().apply { active = true }
        if (entity.id >= entities.size) {
            // entity array is not big enough to store the new entity
            // -> resize by 75% and fill it up again with null values
            repeat(max(1, (entities.size * 0.75f).toInt())) {
                entities.add(null)
            }
            listeners.forEach { it.entityResize(entities.size) }
        }
        entity.init()
        entities.set(entity.id, entity)
        listeners.forEach { it.entityAdded(entity) }
        return entity
    }

    fun free(entity: KECSEntity) {
        listeners.forEach { it.entityRemoved(entity) }
        entities.set(entity.id, null)
        entityPool.free(entity)
    }

    operator fun contains(entity: KECSEntity) = entities[entity.id] == entity

    fun addListener(listener: KECSEntityListener) = listeners.add(listener)
}
