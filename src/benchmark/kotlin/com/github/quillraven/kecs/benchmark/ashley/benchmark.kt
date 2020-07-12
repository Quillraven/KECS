package com.github.quillraven.kecs.benchmark.ashley

import com.badlogic.ashley.core.PooledEngine
import com.github.quillraven.kecs.benchmark.Benchmark
import com.github.quillraven.kecs.benchmark.artemis.BenchmarkArtemis

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

    override fun complex(verify: Boolean): Boolean {
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

        if (verify) {
            for (id in 0 until BenchmarkArtemis.numEntities) {
                val entity = engine.entities[id]
                val cmp1X = entity.getComponent(AshleyComponent1::class.java).x
                val cmp2 = entity.getComponent(AshleyComponent2::class.java)
                val cmp3Counter = entity.getComponent(AshleyComponent3::class.java).counter
                if (cmp1X != 0 || cmp2 != null || cmp3Counter != BenchmarkArtemis.complexIterations) {
                    return false
                }
            }
            return true
        }
        return true
    }
}
