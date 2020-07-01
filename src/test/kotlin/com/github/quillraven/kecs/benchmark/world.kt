package com.github.quillraven.kecs.benchmark

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.utils.Pool
import com.github.quillraven.kecs.World
import kotlin.system.measureTimeMillis

/*
    Benchmarks for the initial setup of the entity world and
    adding/removing entities and components
 */

class AshleyComponent : Component, Pool.Poolable {
    var x = 0
    var y = 0

    override fun reset() {
        x = 0
        y = 0
    }
}

class KECSComponent {
    var x = 0
    var y = 0
}

private const val NUM_RUNS = 10
private const val NUM_ENTITIES = 1000

fun main() {
    benchmarkCreate()
    benchmarkCreateAndRemove()
}

private fun benchmarkCreate() {
    val ashleyRuntimes = LongArray(NUM_RUNS)
    val kecsRuntimes = LongArray(NUM_RUNS)
    for (i in 0 until NUM_RUNS) {
        ashleyRuntimes[i] = measureTimeMillis {
            benchmarkAshleyCreate()
        }
    }
    for (i in 0 until NUM_RUNS) {
        kecsRuntimes[i] = measureTimeMillis {
            benchmarkKECSCreate()
        }
    }

    println("CREATE")
    println("Average time Ashley: ${ashleyRuntimes.average()} ms")
    println("Average time KECS: ${kecsRuntimes.average()} ms")
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

private fun benchmarkAshleyCreate() {
    val engine = PooledEngine(NUM_ENTITIES, NUM_ENTITIES, 0, 0)
    repeat(NUM_ENTITIES) {
        engine.addEntity(engine.createEntity().apply {
            this.add(engine.createComponent(AshleyComponent::class.java))
        })
    }
}

private fun benchmarkAshleyCreateAndRemove() {
    val engine = PooledEngine(NUM_ENTITIES, NUM_ENTITIES, 0, 0)
    repeat(NUM_ENTITIES) {
        engine.addEntity(engine.createEntity().apply {
            this.add(engine.createComponent(AshleyComponent::class.java))
        })
    }
    repeat(NUM_ENTITIES) {
        engine.removeEntity(engine.entities[0])
    }
}

private fun benchmarkKECSCreate() {
    val world = World(NUM_ENTITIES)
    val manager = world.componentManager<KECSComponent>()
    repeat(NUM_ENTITIES) {
        manager.register(world.entity())
    }
}

private fun benchmarkKECSCreateAndRemove() {
    val world = World(NUM_ENTITIES)
    val manager = world.componentManager<KECSComponent>()
    repeat(NUM_ENTITIES) {
        manager.register(world.entity())
    }
    for (id in 0 until NUM_ENTITIES) {
        world.removeEntity(id)
    }
}