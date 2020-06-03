package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool

data class KECSEntity(
    val manager: KECSManager,
    val id: Int,
    var active: Boolean
) : Pool.Poolable {
    inline fun <reified T : KECSComponent> add(init: T.() -> (Unit) = {}): T {
        val component = manager.componentManager.obtain<T>().apply { init() }
        manager.componentManager.add(this, component)
        return component
    }

    // slow version
    operator fun contains(component: KECSComponent) = contains(manager.componentManager.mapper(component::class))

    // fast version
    operator fun contains(mapper: KECSComponentMapper) =
        manager.componentManager.entityComponents[id][mapper.id] != null

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

class KECSEntityManager(
    manager: KECSManager,
    initialEntityCapacity: Int
) {
    val entityPool = KECSEntityPool(manager, initialEntityCapacity)
    val entities = Array<KECSEntity>(false, initialEntityCapacity).apply {
        // fill array with null values to correctly set the size and to be able to call "set(index,value)"
        repeat(initialEntityCapacity) {
            this.add(null)
        }
    }

    fun obtain(): KECSEntity {
        val entity = entityPool.obtain().apply { active = true }
        if (entity.id >= entities.size) {
            // entity array is not big enough to store the new entity
            // -> resize by 75% and fill it up again with null values
            repeat((entities.size * 0.75f).toInt()) {
                entities.add(null)
            }
        }
        entities.set(entity.id, entity)
        return entity
    }

    fun free(entity: KECSEntity) {
        entities.set(entity.id, null)
        entityPool.free(entity)
    }

    operator fun contains(entity: KECSEntity) = entities[entity.id] == entity
}
