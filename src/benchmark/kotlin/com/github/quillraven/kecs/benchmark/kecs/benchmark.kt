package com.github.quillraven.kecs.benchmark.kecs

import com.github.quillraven.kecs.World
import com.github.quillraven.kecs.benchmark.Benchmark
import com.github.quillraven.kecs.benchmark.artemis.BenchmarkArtemis

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

    override fun complex(verify: Boolean): Boolean {
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

        if (verify) {
            for (id in 0 until BenchmarkArtemis.numEntities) {
                val cmp1X = world.componentManager<KECSComponent1>()[id].x
                val hasCmp2 = id in world.componentManager<KECSComponent2>()
                val cmp3Counter = world.componentManager<KECSComponent3>()[id].counter
                if (id % 2 == 0 && (cmp1X != BenchmarkArtemis.complexIterations || hasCmp2 || cmp3Counter != BenchmarkArtemis.complexIterations)) {
                    return false
                } else if (id % 2 == 1 && (cmp1X != 0 || hasCmp2 || cmp3Counter != BenchmarkArtemis.complexIterations)) {
                    return false
                }
            }
            return true
        }
        return true
    }
}
