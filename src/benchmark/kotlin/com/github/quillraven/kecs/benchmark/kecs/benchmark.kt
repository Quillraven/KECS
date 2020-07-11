package com.github.quillraven.kecs.benchmark.kecs

import com.github.quillraven.kecs.World
import com.github.quillraven.kecs.benchmark.Benchmark

object BenchmarkKECS : Benchmark() {
    override fun create() {
        val world = World(numEntities)
        val manager = world.componentManager<KECSComponent1>()
        repeat(numEntities) {
            manager.register(world.entity())
        }
    }

    override fun createAndRemove() {
        val world = World(numEntities)
        val manager = world.componentManager<KECSComponent1>()
        repeat(numEntities) {
            manager.register(world.entity())
        }
        for (id in 0 until numEntities) {
            world.removeEntity(id)
        }
    }

    override fun simple() {
        val world = World(numEntities)
        val manager = world.componentManager<KECSComponent1>()
        world.systems(KECSIteratingSystemSimple(world))
        repeat(numEntities) {
            manager.register(world.entity())
        }
        repeat(simpleIterations) {
            world.update(1f)
        }
    }

    override fun complex() {
        val world = World(numEntities)
        val manager1 = world.componentManager<KECSComponent1>()
        val manager3 = world.componentManager<KECSComponent3>()
        world.systems(
            KECSIteratingSystemComplex1(world),
            KECSIteratingSystemComplex2(world)
        )
        repeat(numEntities) {
            world.entity().apply {
                manager1.register(this)
                manager3.register(this)
            }
        }
        repeat(complexIterations) {
            world.update(1f)
        }
    }
}
