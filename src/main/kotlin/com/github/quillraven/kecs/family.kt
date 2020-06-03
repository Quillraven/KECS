package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import java.util.*

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
