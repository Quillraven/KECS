package com.github.quillraven.kecs.benchmark.artemis

import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.PooledComponent
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.annotations.One
import com.artemis.systems.IteratingSystem

// Component that is used for accessing component data benchmarks
class ArtemisComponent1 : PooledComponent() {
    var x = 0
    var y = 0

    override fun reset() {
        x = 0
        y = 0
    }
}

class ArtemisComponent2 : PooledComponent() {
    override fun reset() = Unit
}

// Component that is used for sorting benchmark
class ArtemisComponent3 : PooledComponent(), Comparable<ArtemisComponent3> {
    var counter = 0

    override fun compareTo(other: ArtemisComponent3) = counter.compareTo(other.counter)

    override fun reset() {
        counter = 0
    }
}

// System that modifies the data of Component1
@All(ArtemisComponent1::class)
class ArtemisIteratingSystemSimple : IteratingSystem() {
    private lateinit var mapper: ComponentMapper<ArtemisComponent1>

    override fun process(entityId: Int) {
        mapper[entityId].x++
    }
}

// System that removes each of its entities every call by adding/removing certain components
// Either adds Component2 or removes Component1
@All(ArtemisComponent1::class)
@Exclude(ArtemisComponent2::class)
@One(ArtemisComponent3::class)
class ArtemisIteratingSystemComplex1 : IteratingSystem() {
    var processCalls = 0
    private lateinit var mapper1: ComponentMapper<ArtemisComponent1>
    private lateinit var mapper2: ComponentMapper<ArtemisComponent2>
    private lateinit var mapper3: ComponentMapper<ArtemisComponent3>

    override fun process(entityId: Int) {
        if (processCalls % 2 == 0) {
            mapper1[entityId].x++
            mapper2.create(entityId)
        } else {
            mapper1.remove(entityId)
        }
        mapper3[entityId].counter++
        ++processCalls
    }
}

// System that makes an entity compatible again with the other complex system
// Removes Component2 and adds Component1
@One(ArtemisComponent1::class, ArtemisComponent2::class, ArtemisComponent3::class)
class ArtemisIteratingSystemComplex2 : BaseEntitySystem() {
    private lateinit var mapper1: ComponentMapper<ArtemisComponent1>
    private lateinit var mapper2: ComponentMapper<ArtemisComponent2>
    private lateinit var mapper3: ComponentMapper<ArtemisComponent3>
    private val comparator = compareBy<Int> { mapper3[it] }
    var processCalls = 0

    override fun processSystem() {
        val ids = subscription.entities.data
        ids.sortedWith(comparator).forEach { process(it) }
    }

    fun process(entityId: Int) {
        mapper2.remove(entityId)
        mapper1.create(entityId)
        ++processCalls
    }
}
