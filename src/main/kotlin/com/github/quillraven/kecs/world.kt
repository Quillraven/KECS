package com.github.quillraven.kecs

import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.ObjectMap

class World(val initialEntityCapacity: Int) {
    private val entities = IntArray(false, initialEntityCapacity).apply {
        repeat(initialEntityCapacity) {
            add(-1)
        }
    }
    private val freeIDs = IntArray(false, initialEntityCapacity)
    private var nextEntityID = 0
    val componentManagers = ObjectMap<Class<*>, ComponentManager<*>>()

    fun entity(): Int {
        return when {
            freeIDs.isEmpty -> {
                entities[nextEntityID] = nextEntityID++
                nextEntityID - 1
            }
            else -> {
                val id = freeIDs.removeIndex(freeIDs.size - 1)
                entities[id] = id
                id
            }
        }
    }

    fun removeEntity(entityID: Int) {
        freeIDs.add(entityID)
        entities[entityID] = -1
    }

    operator fun contains(entityID: Int) = entities[entityID] != -1

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> componentManager(): ComponentManager<T> {
        return when {
            componentManagers.containsKey(T::class.java) -> {
                componentManagers[T::class.java] as ComponentManager<T>
            }
            else -> {
                val manager = ComponentManager(initialEntityCapacity, T::class.java)
                componentManagers.put(T::class.java, manager)
                manager
            }
        }
    }
}