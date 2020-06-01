package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.Pool

data class KECSEntity(
    var id: Int,
    var active: Boolean
) : Pool.Poolable {
    override fun reset() {
        id = -1
        active = false
    }
}

class KECSEntityPool(initialEntityCapacity: Int) : Pool<KECSEntity>(initialEntityCapacity) {
    val recycledIds = IntArray(false, 32)
    var nextId = 0

    override fun newObject(): KECSEntity {
        return if (recycledIds.isEmpty) {
            KECSEntity(nextId++, true)
        } else {
            KECSEntity(recycledIds.pop(), true)
        }
    }

    override fun reset(entity: KECSEntity) {
        recycledIds.add(entity.id)
        super.reset(entity)
    }
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
        val entity = entityPool.obtain()
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

    operator fun contains(entity: KECSEntity) = entity.id >= 0 && entities[entity.id] == entity
}
