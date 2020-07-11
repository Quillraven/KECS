package com.github.quillraven.kecs.benchmark.ashley

import com.badlogic.ashley.core.PooledEngine
import com.github.quillraven.kecs.benchmark.Benchmark

object BenchmarkAshley : Benchmark() {
    override fun create() {
        val engine = PooledEngine(
            numEntities,
            numEntities, 16, 16
        )
        repeat(numEntities) {
            engine.addEntity(engine.createEntity().apply {
                this.add(engine.createComponent(AshleyComponent1::class.java))
            })
        }
    }

    override fun createAndRemove() {
        val engine = PooledEngine(
            numEntities,
            numEntities, 16, 16
        )
        repeat(numEntities) {
            engine.addEntity(engine.createEntity().apply {
                this.add(engine.createComponent(AshleyComponent1::class.java))
            })
        }
        repeat(numEntities) {
            engine.removeEntity(engine.entities[0])
        }
    }

    override fun simple() {
        val engine = PooledEngine(
            numEntities,
            numEntities, 16, 16
        )
        engine.addSystem(AshleyIteratingSystemSimple())
        repeat(numEntities) {
            engine.addEntity(engine.createEntity().apply {
                this.add(engine.createComponent(AshleyComponent1::class.java))
            })
        }
        repeat(simpleIterations) {
            engine.update(1f)
        }
    }

    override fun complex() {
        val engine = PooledEngine(
            numEntities,
            numEntities, 16, 16
        )
        engine.addSystem(AshleyIteratingSystemComplex1())
        engine.addSystem(AshleyIteratingSystemComplex2())
        repeat(numEntities) {
            engine.addEntity(engine.createEntity().apply {
                this.add(engine.createComponent(AshleyComponent1::class.java))
                this.add(engine.createComponent(AshleyComponent3::class.java))
            })
        }
        repeat(complexIterations) {
            engine.update(1f)
        }
    }
}
