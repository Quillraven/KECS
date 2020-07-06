package com.github.quillraven.kecs.benchmark

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Pool
import com.github.quillraven.kecs.ComponentManager
import com.github.quillraven.kecs.World
import kotlin.system.measureTimeMillis

/*
    Benchmarks for the initial setup of the entity world and
    adding/removing entities and components
 */

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

class AshleyComponent3 : Component, Pool.Poolable {
    override fun reset() = Unit
}

class KECSComponent1 {
    var x = 0
    var y = 0
}

class KECSComponent2

class KECSComponent3

class AshleyIteratingSystemSimple : IteratingSystem(Family.all(AshleyComponent1::class.java).get()) {
    private val mapper = ComponentMapper.getFor(AshleyComponent1::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        mapper.get(entity).x++
    }
}

class AshleyIteratingSystemComplex1 : IteratingSystem(
    Family
        .all(AshleyComponent1::class.java)
        .exclude(AshleyComponent2::class.java)
        .one(AshleyComponent3::class.java)
        .get()
) {
    private var counter = 0
    private val mapper = ComponentMapper.getFor(AshleyComponent1::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (counter % 2 == 0) {
            mapper[entity].x++
            entity?.add(engine.createComponent(AshleyComponent2::class.java))
        } else {
            entity?.remove(AshleyComponent1::class.java)
        }
        ++counter
    }
}

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

class KECSIteratingSystemComplex1(
    world: World,
    private val manager1: ComponentManager<KECSComponent1> = world.componentManager(),
    private val manager2: ComponentManager<KECSComponent2> = world.componentManager()
) : com.github.quillraven.kecs.IteratingSystem(
    world.family {
        allOf(KECSComponent1::class)
        noneOf(KECSComponent2::class)
        anyOf(KECSComponent3::class)
    }
) {
    private var counter = 0

    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        if (counter % 2 == 0) {
            manager1[entityID].x++
            manager2.register(entityID)
        } else {
            manager1.deregister(entityID)
        }
        ++counter
    }
}

class KECSIteratingSystemComplex2(
    world: World,
    private val manager1: ComponentManager<KECSComponent1> = world.componentManager(),
    private val manager2: ComponentManager<KECSComponent2> = world.componentManager()
) : com.github.quillraven.kecs.IteratingSystem(
    world.family {
        anyOf(KECSComponent1::class, KECSComponent2::class, KECSComponent3::class)
    }
) {
    override fun updateEntity(world: World, entityID: Int, deltaTime: Float) {
        manager2.deregister(entityID)
        manager1.register(entityID)
    }
}

private const val NUM_RUNS = 10
private const val NUM_ENTITIES = 10000
private const val NUM_SIMPLE_ITERATIONS = 1000
private const val NUM_COMPLEX_ITERATIONS = 100

fun main() {
    benchmarkCreate()
    benchmarkCreateAndRemove()
    benchmarkSimpleIteration()
    benchmarkComplexIteration()
}

private fun benchmarkCreate() {
    val ashleyTimes = LongArray(NUM_RUNS)
    val kecsTimes = LongArray(NUM_RUNS)
    for (i in 0 until NUM_RUNS) {
        ashleyTimes[i] = measureTimeMillis {
            benchmarkAshleyCreate()
        }
    }
    for (i in 0 until NUM_RUNS) {
        kecsTimes[i] = measureTimeMillis {
            benchmarkKECSCreate()
        }
    }

    println("CREATE")
    println("Average time Ashley: ${ashleyTimes.average()} ms")
    println("Average time KECS: ${kecsTimes.average()} ms")
}

private fun benchmarkCreateAndRemove() {
    val ashleyRuntimes = LongArray(NUM_RUNS)
    val kecsRuntimes = LongArray(NUM_RUNS)
    for (i in 0 until NUM_RUNS) {
        ashleyRuntimes[i] = measureTimeMillis {
            benchmarkAshleyCreateAndRemove()
        }
    }
    for (i in 0 until NUM_RUNS) {
        kecsRuntimes[i] = measureTimeMillis {
            benchmarkKECSCreateAndRemove()
        }
    }

    println("CREATE AND REMOVE")
    println("Average time Ashley: ${ashleyRuntimes.average()} ms")
    println("Average time KECS: ${kecsRuntimes.average()} ms")
}

private fun benchmarkSimpleIteration() {
    val ashleyTimes = LongArray(NUM_RUNS)
    val kecsTimes = LongArray(NUM_RUNS)
    for (i in 0 until NUM_RUNS) {
        ashleyTimes[i] = measureTimeMillis {
            benchmarkAshleySimpleIteration()
        }
    }
    for (i in 0 until NUM_RUNS) {
        kecsTimes[i] = measureTimeMillis {
            benchmarkKECSSimpleIteration()
        }
    }

    println("SIMPLE ITERATION")
    println("Average time Ashley: ${ashleyTimes.average()} ms")
    println("Average time KECS: ${kecsTimes.average()} ms")
}

private fun benchmarkComplexIteration() {
    val ashleyTimes = LongArray(NUM_RUNS)
    val kecsTimes = LongArray(NUM_RUNS)
    for (i in 0 until NUM_RUNS) {
        ashleyTimes[i] = measureTimeMillis {
            benchmarkAshleyComplexIteration()
        }
    }
    for (i in 0 until NUM_RUNS) {
        kecsTimes[i] = measureTimeMillis {
            benchmarkKECSComplexIteration()
        }
    }

    println("COMPLEX ITERATION")
    println("Average time Ashley: ${ashleyTimes.average()} ms")
    println("Average time KECS: ${kecsTimes.average()} ms")
}

private fun benchmarkAshleyCreate() {
    val engine = PooledEngine(NUM_ENTITIES, NUM_ENTITIES, 16, 16)
    repeat(NUM_ENTITIES) {
        engine.addEntity(engine.createEntity().apply {
            this.add(engine.createComponent(AshleyComponent1::class.java))
        })
    }
}

private fun benchmarkAshleyCreateAndRemove() {
    val engine = PooledEngine(NUM_ENTITIES, NUM_ENTITIES, 16, 16)
    repeat(NUM_ENTITIES) {
        engine.addEntity(engine.createEntity().apply {
            this.add(engine.createComponent(AshleyComponent1::class.java))
        })
    }
    repeat(NUM_ENTITIES) {
        engine.removeEntity(engine.entities[0])
    }
}

private fun benchmarkAshleySimpleIteration() {
    val engine = PooledEngine(NUM_ENTITIES, NUM_ENTITIES, 16, 16)
    engine.addSystem(AshleyIteratingSystemSimple())
    repeat(NUM_ENTITIES) {
        engine.addEntity(engine.createEntity().apply {
            this.add(engine.createComponent(AshleyComponent1::class.java))
        })
    }
    repeat(NUM_SIMPLE_ITERATIONS) {
        engine.update(1 / 60f)
    }
}

private fun benchmarkAshleyComplexIteration() {
    val engine = PooledEngine(NUM_ENTITIES, NUM_ENTITIES, 16, 16)
    engine.addSystem(AshleyIteratingSystemComplex1())
    engine.addSystem(AshleyIteratingSystemComplex2())
    repeat(NUM_ENTITIES) {
        engine.addEntity(engine.createEntity().apply {
            this.add(engine.createComponent(AshleyComponent1::class.java))
            this.add(engine.createComponent(AshleyComponent3::class.java))
        })
    }
    repeat(NUM_COMPLEX_ITERATIONS) {
        engine.update(1 / 60f)
    }
}

private fun benchmarkKECSCreate() {
    val world = World(NUM_ENTITIES)
    val manager = world.componentManager<KECSComponent1>()
    repeat(NUM_ENTITIES) {
        manager.register(world.entity())
    }
}

private fun benchmarkKECSCreateAndRemove() {
    val world = World(NUM_ENTITIES)
    val manager = world.componentManager<KECSComponent1>()
    repeat(NUM_ENTITIES) {
        manager.register(world.entity())
    }
    for (id in 0 until NUM_ENTITIES) {
        world.removeEntity(id)
    }
}

private fun benchmarkKECSSimpleIteration() {
    val world = World(NUM_ENTITIES)
    val manager = world.componentManager<KECSComponent1>()
    world.systems(KECSIteratingSystemSimple(world))
    repeat(NUM_ENTITIES) {
        manager.register(world.entity())
    }
    repeat(NUM_SIMPLE_ITERATIONS) {
        world.update(1 / 60f)
    }
}

private fun benchmarkKECSComplexIteration() {
    val world = World(NUM_ENTITIES)
    val manager1 = world.componentManager<KECSComponent1>()
    val manager3 = world.componentManager<KECSComponent3>()
    world.systems(
        KECSIteratingSystemComplex1(world),
        KECSIteratingSystemComplex2(world)
    )
    repeat(NUM_ENTITIES) {
        world.entity().apply {
            manager1.register(this)
            manager3.register(this)
        }
    }
    repeat(NUM_COMPLEX_ITERATIONS) {
        world.update(1 / 60f)
    }
}
