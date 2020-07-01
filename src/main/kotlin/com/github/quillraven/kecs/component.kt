package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.OrderedSet

interface ComponentListener {
    fun componentAdded(entityID: Int, manager: ComponentManager<*>)
    fun componentRemoved(entityID: Int, manager: ComponentManager<*>)
}

class ComponentManager<T>(
    entityCapacity: Int,
    val type: Class<T>
) : EntityListener {
    private val components = Array<T>(false, entityCapacity).apply {
        repeat(entityCapacity) {
            add(null)
        }
    }
    private val freeComponents = Array<T>(false, entityCapacity)
    private val listeners = OrderedSet<ComponentListener>().apply {
        orderedItems().ordered = false
    }

    fun register(entityID: Int): T {
        if (components[entityID] == null) {
            when {
                freeComponents.isEmpty -> {
                    components[entityID] = type.newInstance()
                }
                else -> {
                    components[entityID] = freeComponents.removeIndex(freeComponents.size - 1)
                }
            }
            listeners.forEach { it.componentAdded(entityID, this) }
        }

        return components[entityID]
    }

    fun unregister(entityID: Int) {
        if (components[entityID] != null) {
            listeners.forEach { it.componentRemoved(entityID, this) }
            freeComponents.add(components[entityID])
            components[entityID] = null
        }
    }

    operator fun contains(entityID: Int) = components[entityID] != null

    operator fun get(entityID: Int): T =
        components[entityID]
            ?: throw KotlinNullPointerException("Entity $entityID does not have a component of type $type")

    fun addListener(listener: ComponentListener) = listeners.add(listener)

    fun removeListener(listener: ComponentListener) = listeners.remove(listener)

    override fun entityAdded(entityID: Int) = Unit

    override fun entityRemoved(entityID: Int) = unregister(entityID)
}
