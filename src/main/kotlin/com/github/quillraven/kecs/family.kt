package com.github.quillraven.kecs

import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.OrderedSet
import kotlin.reflect.KClass

@DslMarker
annotation class FamilyDsl

@FamilyDsl
object FamilyBuilder {
    lateinit var family: Family
    lateinit var world: World

    fun allOf(vararg componentTypes: KClass<*>) {
        componentTypes.forEach {
            val manager = world.componentManager(it.java)
            family.allOf.add(manager)
            manager.addListener(family)
        }
    }

    fun noneOf(vararg componentTypes: KClass<*>) {
        componentTypes.forEach {
            val manager = world.componentManager(it.java)
            family.noneOf.add(manager)
            manager.addListener(family)
        }
    }

    fun anyOf(vararg componentTypes: KClass<*>) {
        componentTypes.forEach {
            val manager = world.componentManager(it.java)
            family.anyOf.add(manager)
            manager.addListener(family)
        }
    }
}

private enum class EntityUpdateOperation {
    ADD, REMOVE
}

data class Family(
    private val world: World,
    val allOf: OrderedSet<ComponentManager<*>> = OrderedSet<ComponentManager<*>>().apply {
        orderedItems().ordered = false
    },
    val noneOf: OrderedSet<ComponentManager<*>> = OrderedSet<ComponentManager<*>>().apply {
        orderedItems().ordered = false
    },
    val anyOf: OrderedSet<ComponentManager<*>> = OrderedSet<ComponentManager<*>>().apply {
        orderedItems().ordered = false
    }
) : ComponentListener {
    private val entities: OrderedSet<Int> = OrderedSet<Int>(world.initialEntityCapacity).apply {
        orderedItems().ordered = false
    }
    private val entityUpdateOperations = IntMap<EntityUpdateOperation>(world.initialEntityCapacity)
    private var iterating = false

    operator fun contains(entityID: Int): Boolean {
        allOf.forEach { manager ->
            if (entityID !in manager) {
                return false
            }
        }

        noneOf.forEach { manager ->
            if (entityID in manager) {
                return false
            }
        }

        anyOf.forEach { manager ->
            if (entityID in manager) {
                return true
            }
        }

        return anyOf.isEmpty
    }

    override fun componentAdded(entityID: Int, manager: ComponentManager<*>) = componentRemoved(entityID, manager)

    override fun componentRemoved(entityID: Int, manager: ComponentManager<*>) {
        if (entityID in this) {
            if (iterating) {
                entityUpdateOperations.put(entityID, EntityUpdateOperation.ADD)
            } else {
                entities.add(entityID)
            }
        } else {
            if (iterating) {
                entityUpdateOperations.put(entityID, EntityUpdateOperation.REMOVE)
            } else {
                entities.remove(entityID)
            }
        }
    }

    fun iterate(action: (Int) -> Unit) {
        iterating = true
        entities.forEach { action(it) }
        iterating = false
        entityUpdateOperations.forEach { entry ->
            if (entry.value == EntityUpdateOperation.ADD) {
                entities.add(entry.key)
            } else {
                entities.remove(entry.key)
            }
        }
    }
}
