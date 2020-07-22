package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.OrderedSet
import java.util.*
import kotlin.reflect.KClass

@DslMarker
annotation class FamilyDsl

@FamilyDsl
class FamilyBuilder(
    private val world: World,
    private val entityComponents: Array<BitSet>,
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
        val allBitSet = BitSet().apply {
            allOf.forEach { this.set(it.id) }
        }
        val noneBitSet = BitSet().apply {
            noneOf.forEach { this.set(it.id) }
        }
        val anyBitSet = BitSet().apply {
            anyOf.forEach { this.set(it.id) }
        }
        val family = Family(entityComponents, allBitSet, noneBitSet, anyBitSet)
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

data class Family(
    private val entityComponents: Array<BitSet>,
    private val allOf: BitSet,
    private val noneOf: BitSet,
    private val anyOf: BitSet
) : ComponentListener {
    private val allOfIds = IntArray(false, allOf.cardinality()).apply {
        var idx = allOf.nextSetBit(0)
        while (idx >= 0) {
            add(idx)
            if (idx == Integer.MAX_VALUE) {
                break // or (idx+1) would overflow
            }
            idx = allOf.nextSetBit(idx + 1)
        }
    }
    val entitiesToUpdate = BitSet(entityComponents.size)
    val activeIDs = IntArray(false, entityComponents.size)

    operator fun contains(entityID: Int): Boolean {
        val components = entityComponents[entityID]

        if (!allOf.isEmpty) {
            for (i in 0 until allOfIds.size) {
                if (!components[allOfIds[i]]) {
                    return false
                }
            }
        }

        return (noneOf.isEmpty || !noneOf.intersects(components))
                && (anyOf.isEmpty || anyOf.intersects(components))
    }

    override fun componentAdded(entityID: Int, manager: ComponentManager<*>) = entitiesToUpdate.set(entityID)

    override fun componentRemoved(entityID: Int, manager: ComponentManager<*>) = entitiesToUpdate.set(entityID)

    inline fun iterate(action: (Int) -> Unit) {
        if (!entitiesToUpdate.isEmpty) {
            // entity component configuration changes -> evaluate once more which entities belong to the family
            // Note: it is faster to simply rebuild the active IDs from scratch instead of updating them
            for (i in 0 until activeIDs.size) {
                entitiesToUpdate.set(activeIDs[i])
            }
            activeIDs.clear()
            var entityID = entitiesToUpdate.nextSetBit(0)
            while (entityID >= 0) {
                if (entityID in this) {
                    activeIDs.add(entityID)
                }

                if (entityID == Integer.MAX_VALUE) {
                    break // or (entityID+1) would overflow
                }
                entityID = entitiesToUpdate.nextSetBit(entityID + 1)
            }
            entitiesToUpdate.clear()
        }

        // execute the action for all entities of the family
        for (i in 0 until activeIDs.size) {
            action(activeIDs[i])
        }
    }
}
