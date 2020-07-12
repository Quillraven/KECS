package com.github.quillraven.kecs.benchmark.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Pool

// Component that is used for accessing component data benchmarks
class AshleyComponent1 : Component, Pool.Poolable {
    var x = 0
    var y = 0

    override fun reset() {
        x = 0
        y = 0
    }
}

class AshleyComponent2 : Component, Pool.Poolable {
    override fun reset() = Unit
}

// Component that is used for sorting benchmark
class AshleyComponent3 : Component, Pool.Poolable, Comparable<AshleyComponent3> {
    var counter = 0

    override fun reset() {
        counter = 0
    }

    override fun compareTo(other: AshleyComponent3) = counter.compareTo(other.counter)

    companion object {
        val mapper: ComponentMapper<AshleyComponent3> = ComponentMapper.getFor(
            AshleyComponent3::class.java
        )
    }
}

// System that modifies the data of Component1
class AshleyIteratingSystemSimple : IteratingSystem(Family.all(AshleyComponent1::class.java).get()) {
    private val mapper = ComponentMapper.getFor(AshleyComponent1::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        mapper.get(entity).x++
    }
}

// System that removes each of its entities every call by adding/removing certain components
// Either adds Component2 or removes Component1
class AshleyIteratingSystemComplex1 : IteratingSystem(
    Family
        .all(AshleyComponent1::class.java)
        .exclude(AshleyComponent2::class.java)
        .one(AshleyComponent3::class.java)
        .get()
) {
    private var processEntityCalls = 0
    private val mapper = ComponentMapper.getFor(AshleyComponent1::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (processEntityCalls % 2 == 0) {
            mapper[entity].x++
            entity?.add(engine.createComponent(AshleyComponent2::class.java))
        } else {
            entity?.remove(AshleyComponent1::class.java)
        }
        AshleyComponent3.mapper.get(entity).counter++
        ++processEntityCalls
    }
}

// System that makes an entity compatible again with the other complex system
// Removes Component2 and adds Component1
class AshleyIteratingSystemComplex2 : IteratingSystem(
    Family
        .one(AshleyComponent1::class.java, AshleyComponent2::class.java, AshleyComponent3::class.java)
        .get()
) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        entity?.remove(AshleyComponent2::class.java)
        entity?.add(engine.createComponent(AshleyComponent1::class.java))
    }
}
