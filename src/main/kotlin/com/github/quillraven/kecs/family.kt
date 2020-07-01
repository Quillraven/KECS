package com.github.quillraven.kecs

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
    val entities: OrderedSet<Int> = OrderedSet<Int>(world.initialEntityCapacity).apply {
        orderedItems().ordered = false
    }
    val entitiesToAdd: OrderedSet<Int> = OrderedSet<Int>(world.initialEntityCapacity).apply {
        orderedItems().ordered = false
    }
    val entitiesToRemove: OrderedSet<Int> = OrderedSet<Int>(world.initialEntityCapacity).apply {
        orderedItems().ordered = false
    }
    val isDirty: Boolean
        get() = !entitiesToAdd.isEmpty || !entitiesToRemove.isEmpty

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
            entitiesToAdd.add(entityID)
            entitiesToRemove.remove(entityID)
        } else {
            entitiesToRemove.add(entityID)
            entitiesToAdd.remove(entityID)
        }
    }

    fun update() {
        entitiesToRemove.forEach { entities.remove(it) }
        entitiesToAdd.forEach { entities.add(it) }
    }
}
