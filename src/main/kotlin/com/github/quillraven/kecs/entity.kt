package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import java.util.*
import kotlin.math.max
import kotlin.reflect.KClass

class KECSEntityCreateDSL(
    private val componentManager: KECSComponentManager,
    private val components: Array<KECSComponent>
) {
    inline fun <reified T : KECSComponent> with(noinline init: T.() -> (Unit) = {}): T = with(T::class, init)

    @Suppress("UNCHECKED_CAST")
    fun <T : KECSComponent> with(type: KClass<T>, init: T.() -> (Unit) = {}): T {
        val component = (componentManager.obtain(type) as T).apply(init)
        components.add(component)
        return component
    }
}

class KECSEntityUpdateDSL(
    private val componentManager: KECSComponentManager
) {
    lateinit var entity: KECSEntity

    // slow version
    inline fun <reified T : KECSComponent> add(noinline init: T.() -> (Unit) = {}) = add(T::class, init)

    // slow version
    @Suppress("UNCHECKED_CAST")
    fun <T : KECSComponent> add(type: KClass<T>, init: T.() -> (Unit) = {}): T =
        add(type, componentManager.mapper(type), init)

    // fast version
    inline fun <reified T : KECSComponent> add(mapper: KECSComponentMapper, noinline init: T.() -> (Unit) = {}) =
        add(T::class, mapper, init)

    // fast version
    @Suppress("UNCHECKED_CAST")
    fun <T : KECSComponent> add(type: KClass<T>, mapper: KECSComponentMapper, init: T.() -> (Unit) = {}): T {
        val component = (componentManager.obtain(type) as T).apply(init)
        componentManager.add(entity, mapper, component)
        return component
    }

    // slow version
    inline fun <reified T : KECSComponent> remove() = remove(T::class)

    // slow version
    fun <T : KECSComponent> remove(type: KClass<T>) = remove(type, componentManager.mapper(type))

    // fast version
    inline fun <reified T : KECSComponent> remove(mapper: KECSComponentMapper) = remove(T::class, mapper)

    // fast version
    fun <T : KECSComponent> remove(type: KClass<T>, mapper: KECSComponentMapper) =
        remove(componentManager.get(type, entity, mapper))

    // fastest version
    fun remove(component: KECSComponent) = componentManager.remove(entity, component)
}

data class KECSEntity(
    private val componentManager: KECSComponentManager,
    private val entityManager: KECSEntityManager,
    val id: Int,
    var active: Boolean
) : Pool.Poolable {
    val componentBits: BitSet
        get() = componentManager.entityComponentBits[id]
    val components: Array<KECSComponent>
        get() = componentManager.entityComponents[id]

    fun updateEntity(update: KECSEntityUpdateDSL.() -> (Unit)) = entityManager.update(this, update)

    fun free() = entityManager.free(this)

    // slow version
    operator fun contains(component: KECSComponent) = contains(componentManager.mapper(component::class))

    // fast version
    operator fun contains(mapper: KECSComponentMapper) =
        componentManager.entityComponents[id][mapper.id] != null

    override fun reset() {
        active = false
    }
}

class KECSEntityPool(
    private val componentManager: KECSComponentManager,
    private val entityManager: KECSEntityManager,
    initialEntityCapacity: Int
) : Pool<KECSEntity>(initialEntityCapacity) {
    var nextId = 0

    override fun newObject(): KECSEntity = KECSEntity(componentManager, entityManager, nextId++, true)
}

interface KECSEntityListener {
    fun entityAdded(entity: KECSEntity) = Unit
    fun entityRemoved(entity: KECSEntity) = Unit
    fun entityResize(newSize: Int) = Unit
    fun entityComponentsUpdated(entity: KECSEntity) = Unit
}

class KECSEntityManager(
    private val componentManager: KECSComponentManager,
    initialEntityCapacity: Int
) {
    private val listeners = Array<KECSEntityListener>(false, 16)
    val entityPool = KECSEntityPool(componentManager, this, initialEntityCapacity)
    val entities = Array<KECSEntity>(false, initialEntityCapacity).apply {
        // fill array with null values to correctly set the size and to be able to call "set(index,value)"
        repeat(initialEntityCapacity) {
            this.add(null)
        }
    }
    private val entityUpdateDSL = KECSEntityUpdateDSL(componentManager)

    fun obtain(components: Array<KECSComponent>? = null): KECSEntity {
        val entity = entityPool.obtain().apply { active = true }
        if (entity.id >= entities.size) {
            // entity array is not big enough to store the new entity
            // -> resize by 75% and fill it up again with null values
            repeat(max(1, (entities.size * 0.75f).toInt())) {
                entities.add(null)
            }
            listeners.forEach { it.entityResize(entities.size) }
        }
        components?.forEach { componentManager.add(entity, it) }
        entities.set(entity.id, entity)
        listeners.forEach { it.entityAdded(entity) }
        return entity
    }

    fun update(entity: KECSEntity, update: KECSEntityUpdateDSL.() -> (Unit)) {
        entityUpdateDSL.entity = entity
        entityUpdateDSL.update()
        listeners.forEach { it.entityComponentsUpdated(entity) }
    }

    fun free(entity: KECSEntity) {
        listeners.forEach { it.entityRemoved(entity) }
        entities.set(entity.id, null)
        entityPool.free(entity)
    }

    operator fun contains(entity: KECSEntity) = entities[entity.id] == entity

    fun addListener(listener: KECSEntityListener) = listeners.add(listener)
}
