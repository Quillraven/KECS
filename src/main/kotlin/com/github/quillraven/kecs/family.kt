package com.github.quillraven.kecs

import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.OrderedSet
import kotlin.reflect.KClass

@DslMarker
annotation class FamilyDsl

@FamilyDsl
class FamilyBuilder(
    private val world: World,
    private val families: ObjectSet<Family>
) {
    private val allOf = OrderedSet<ComponentManager<*>>().apply {
        orderedItems().ordered = false
    }
    private val noneOf = OrderedSet<ComponentManager<*>>().apply {
        orderedItems().ordered = false
    }
    private val anyOf = OrderedSet<ComponentManager<*>>().apply {
        orderedItems().ordered = false
    }

    fun allOf(vararg componentTypes: KClass<*>) {
        componentTypes.forEach {
            allOf.add(world.componentManager(it.java))
        }
    }

    fun noneOf(vararg componentTypes: KClass<*>) {
        componentTypes.forEach {
            noneOf.add(world.componentManager(it.java))
        }
    }

    fun anyOf(vararg componentTypes: KClass<*>) {
        componentTypes.forEach {
            anyOf.add(world.componentManager(it.java))
        }
    }

    fun build(): Family {
        val family = Family(world, allOf, noneOf, anyOf)
        if (families.contains(family)) {
            return families.get(family)
        }
        allOf.forEach { it.addListener(family) }
        noneOf.forEach { it.addListener(family) }
        anyOf.forEach { it.addListener(family) }
        families.add(family)
        return family
    }
}

private enum class EntityUpdateOperation {
    ADD, REMOVE
}

data class Family(
    private val world: World,
    private val allOf: OrderedSet<ComponentManager<*>>,
    private val noneOf: OrderedSet<ComponentManager<*>>,
    private val anyOf: OrderedSet<ComponentManager<*>>
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

    fun sort(comparator: Comparator<in Int>) = entities.orderedItems().sort(comparator)

    fun iterate(action: (Int) -> Unit) {
        iterating = true
        entities.forEach { action(it) }
        iterating = false
        if (!entityUpdateOperations.isEmpty) {
            val iterator = entityUpdateOperations.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.value == EntityUpdateOperation.ADD) {
                    entities.add(entry.key)
                } else {
                    entities.remove(entry.key)
                }
                iterator.remove()
            }
        }
    }
}
