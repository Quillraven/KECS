package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import java.util.*
import kotlin.reflect.KClass

class KECSFamilyDSL(
    private val allMappers: Array<KECSComponentMapper>,
    private val noneMappers: Array<KECSComponentMapper>,
    private val anyMappers: Array<KECSComponentMapper>,
    private val componentManager: KECSComponentManager
) {

    fun allOf(vararg types: KClass<out KECSComponent>) {
        allMappers.clear()
        types.forEach { allMappers.add(componentManager.mapper(it)) }
    }

    fun noneOf(vararg types: KClass<out KECSComponent>) {
        noneMappers.clear()
        types.forEach { noneMappers.add(componentManager.mapper(it)) }
    }

    fun anyOf(vararg types: KClass<out KECSComponent>) {
        anyMappers.clear()
        types.forEach { anyMappers.add(componentManager.mapper(it)) }
    }
}

data class KECSFamily(
    val allSet: BitSet,
    val noneSet: BitSet,
    val anySet: BitSet
) {
    fun clear() {
        allSet.clear()
        noneSet.clear()
        anySet.clear()
    }

    operator fun contains(components: BitSet): Boolean {
        if (!allSet.isEmpty) {
            for (i in 0 until allSet.length()) {
                if (allSet[i] && !components[i]) {
                    return false
                }
            }
        }

        if (!noneSet.isEmpty && noneSet.intersects(components)) {
            return false
        }

        if (!anySet.isEmpty && !anySet.intersects(components)) {
            return false
        }

        return true
    }
}

class KECSFamilyManager {
    private val tmpFamily = KECSFamily(BitSet(), BitSet(), BitSet())
    val familyEntities = ObjectMap<KECSFamily, Array<KECSEntity>>()

    fun family(
        all: Array<KECSComponentMapper>? = null,
        none: Array<KECSComponentMapper>? = null,
        any: Array<KECSComponentMapper>? = null,
        initialEntityCapacity: Int
    ): KECSFamily {
        tmpFamily.run {
            clear()
            all?.forEach { allSet.set(it.id) }
            none?.forEach { noneSet.set(it.id) }
            any?.forEach { anySet.set(it.id) }
        }

        if (!familyEntities.containsKey(tmpFamily)) {
            familyEntities.put(
                KECSFamily(
                    tmpFamily.allSet.clone() as BitSet,
                    tmpFamily.noneSet.clone() as BitSet,
                    tmpFamily.anySet.clone() as BitSet
                ), Array(false, initialEntityCapacity)
            )
        }

        return familyEntities.keys().first { it == tmpFamily }
    }

    operator fun contains(family: KECSFamily) = familyEntities.containsKey(family)
}
