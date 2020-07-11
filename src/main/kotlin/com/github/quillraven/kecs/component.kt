package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.OrderedSet
import kotlin.math.max

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
    val size: Int
        get() = components.size

    fun register(entityID: Int): T {
        if (entityID >= components.size) {
            // initial entity capacity exceeded -> resize by 75%
            repeat(max(1, (entityID * 0.75f).toInt())) {
                components.add(null)
            }
        }

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

    fun deregister(entityID: Int) {
        if (components[entityID] != null) {
            freeComponents.add(components[entityID])
            components[entityID] = null
            listeners.forEach { it.componentRemoved(entityID, this) }
        }
    }

    operator fun contains(entityID: Int) = components[entityID] != null

    operator fun get(entityID: Int): T =
        components[entityID]
            ?: throw MissingComponentException(entityID, type)

    fun addListener(listener: ComponentListener) = listeners.add(listener)

    fun removeListener(listener: ComponentListener) = listeners.remove(listener)

    operator fun contains(listener: ComponentListener) = listeners.contains(listener)

    override fun entityAdded(entityID: Int) = Unit

    override fun entityRemoved(entityID: Int) = deregister(entityID)
}
