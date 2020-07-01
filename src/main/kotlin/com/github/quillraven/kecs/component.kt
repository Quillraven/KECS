package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array

class ComponentManager<T>(
    entityCapacity: Int,
    private val type: Class<T>
) {
    private val components = Array<T>(false, entityCapacity).apply {
        repeat(entityCapacity) {
            add(null)
        }
    }
    private val freeComponents = Array<T>(false, entityCapacity)

    fun register(entityID: Int): T {
        when {
            freeComponents.isEmpty -> {
                components[entityID] = type.newInstance()
            }
            else -> {
                components[entityID] = freeComponents.removeIndex(freeComponents.size - 1)
            }
        }

        return components[entityID]
    }

    fun unregister(entityID: Int) {
        freeComponents.add(components[entityID])
        components[entityID] = null
    }

    operator fun contains(entityID: Int) = components[entityID] != null

    operator fun get(entityID: Int): T =
        components[entityID]
            ?: throw KotlinNullPointerException("Entity $entityID does not have a component of type $type")
}