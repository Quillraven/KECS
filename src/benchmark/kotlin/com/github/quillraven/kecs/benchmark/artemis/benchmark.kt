package com.github.quillraven.kecs.benchmark.artemis

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.github.quillraven.kecs.benchmark.Benchmark
import com.github.quillraven.kecs.benchmark.kecs.BenchmarkKECS

object BenchmarkArtemis : Benchmark() {
    override fun create() {
        val world = World()
        repeat(numEntities) {
            world.createEntity().edit().create(ArtemisComponent1::class.java)
        }
    }

    override fun createAndRemove() {
        val world = World()
        repeat(numEntities) {
            world.createEntity().edit().create(ArtemisComponent1::class.java)
        }
        for (id in 0 until BenchmarkKECS.numEntities) {
            world.delete(id)
        }
    }

    override fun simple() {
        val world = World(WorldConfigurationBuilder().run {
            with(ArtemisIteratingSystemSimple())
            build()
        })
        repeat(numEntities) {
            world.createEntity().edit().create(ArtemisComponent1::class.java)
        }
        repeat(simpleIterations) {
            world.delta = 1f
            world.process()
        }
    }

    override fun complex() {
        val world = World(WorldConfigurationBuilder().run {
            with(ArtemisIteratingSystemComplex1())
            with(ArtemisIteratingSystemComplex2())
            build()
        })
        repeat(numEntities) {
            world.createEntity().edit().apply {
                create(ArtemisComponent1::class.java)
                create(ArtemisComponent3::class.java)
            }
        }
        repeat(complexIterations) {
            world.delta = 1f
            world.process()
        }
    }
}
