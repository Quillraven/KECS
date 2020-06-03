package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool

data class KECSEntity(
    val id: Int,
    var active: Boolean
) : Pool.Poolable {
    override fun reset() {
        active = false
    }
}

class KECSEntityPool(initialEntityCapacity: Int) : Pool<KECSEntity>(initialEntityCapacity) {
    var nextId = 0

    override fun newObject(): KECSEntity = KECSEntity(nextId++, true)
}

class KECSEntityManager(initialEntityCapacity: Int) {
    val entityPool = KECSEntityPool(initialEntityCapacity)
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
