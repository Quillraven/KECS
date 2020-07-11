package com.github.quillraven.kecs.benchmark.kecs

import com.github.quillraven.kecs.ComponentManager
import com.github.quillraven.kecs.World

// Component that is used for accessing component data benchmarks
class KECSComponent1 {
    var x = 0
    var y = 0
}

class KECSComponent2

// Component that is used for sorting benchmark
class KECSComponent3 : Comparable<KECSComponent3> {
    var counter = 0

    override fun compareTo(other: KECSComponent3) = counter.compareTo(other.counter)
}

// System that modifies the data of Component1
class KECSIteratingSystemSimple(
    world: World,
    private val manager: ComponentManager<KECSComponent1> = world.componentManager()
) : com.github.quillraven.kecs.IteratingSystem(
    world.family { allOf(KECSComponent1::class) }
) {
    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        manager[entityID].x++
    }
}

// System that removes each of its entities every call by adding/removing certain components
// Either adds Component2 or removes Component1
class KECSIteratingSystemComplex1(
    world: World,
    private val manager1: ComponentManager<KECSComponent1> = world.componentManager(),
    private val manager2: ComponentManager<KECSComponent2> = world.componentManager(),
    private val manager3: ComponentManager<KECSComponent3> = world.componentManager()
) : com.github.quillraven.kecs.IteratingSystem(
    world.family {
        allOf(KECSComponent1::class)
        noneOf(KECSComponent2::class)
        anyOf(KECSComponent3::class)
    }
) {
    var updateEntityCalls = 0

    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        if (updateEntityCalls % 2 == 0) {
            manager1[entityID].x++
            manager2.register(entityID)
        } else {
            manager1.deregister(entityID)
        }
        manager3[entityID].counter++
        ++updateEntityCalls
    }
}

// System that makes an entity compatible again with the other complex system
// Removes Component2 and adds Component1
class KECSIteratingSystemComplex2(
    world: World,
    private val manager1: ComponentManager<KECSComponent1> = world.componentManager(),
    private val manager2: ComponentManager<KECSComponent2> = world.componentManager(),
    private val manager3: ComponentManager<KECSComponent3> = world.componentManager()
) : com.github.quillraven.kecs.IteratingSystem(
    world.family {
        anyOf(KECSComponent1::class, KECSComponent2::class, KECSComponent3::class)
    }
) {
    private val comparator = compareBy<Int> { manager3[it] }
    var updateEntityCalls = 0

    override fun update(world: World, deltaTime: Float) {
        sort(comparator)
        super.update(world, deltaTime)
    }

    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        manager2.deregister(entityID)
        manager1.register(entityID)
        ++updateEntityCalls
    }
}
