package com.github.quillraven.kecs.benchmark.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Pool
import kotlin.system.measureTimeMillis


private const val NUM_ENTITIES = 1000
private const val NUM_COMPONENTS_PER_ENTITY = 20
private const val NUM_TEST_RUNS = 100
private const val NUM_UPDATE_CALLS = 3600

class Cmp1 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp1::class.java)
    }
}

class Cmp2 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp2::class.java)
    }
}

class Cmp3 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp3::class.java)
    }
}

class Cmp4 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp4::class.java)
    }
}

class Cmp5 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp5::class.java)
    }
}

class Cmp6 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp6::class.java)
    }
}

class Cmp7 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp7::class.java)
    }
}

class Cmp8 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp8::class.java)
    }
}

class Cmp9 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp9::class.java)
    }
}

class Cmp10 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp10::class.java)
    }
}

class Cmp11 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp11::class.java)
    }
}

class Cmp12 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp12::class.java)
    }
}

class Cmp13 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp13::class.java)
    }
}

class Cmp14 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp14::class.java)
    }
}

class Cmp15 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp15::class.java)
    }
}

class Cmp16 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp16::class.java)
    }
}

class Cmp17 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp17::class.java)
    }
}

class Cmp18 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp18::class.java)
    }
}

class Cmp19 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp19::class.java)
    }
}

class Cmp20 : Component, Pool.Poolable {
    var x = 0f

    override fun reset() {
        x = 0f
    }

    companion object {
        val mapper = ComponentMapper.getFor(Cmp20::class.java)
    }
}

class IteratingSystem1 : IteratingSystem(
    Family.all(Cmp1::class.java, Cmp2::class.java).exclude(Cmp3::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp1::class.java).x++
    }
}

class IteratingSystem2 : IteratingSystem(
    Family.all(Cmp3::class.java, Cmp4::class.java).exclude(Cmp5::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp3::class.java).x++
    }
}

class IteratingSystem3 : IteratingSystem(
    Family.all(Cmp5::class.java, Cmp6::class.java).exclude(Cmp7::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp5::class.java).x++
    }
}

class IteratingSystem4 : IteratingSystem(
    Family.all(Cmp8::class.java, Cmp9::class.java).exclude(Cmp10::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp8::class.java).x++
    }
}

class IteratingSystem5 : IteratingSystem(
    Family.all(Cmp11::class.java, Cmp12::class.java).exclude(Cmp13::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp11::class.java).x++
    }
}

class IteratingSystem6 : IteratingSystem(
    Family.all(Cmp14::class.java, Cmp15::class.java).exclude(Cmp16::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp14::class.java).x++
    }
}

class IteratingSystem7 : IteratingSystem(
    Family.all(Cmp17::class.java, Cmp18::class.java).exclude(Cmp19::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp17::class.java).x++
    }
}

class IteratingSystem8 : IteratingSystem(
    Family.one(Cmp1::class.java, Cmp10::class.java, Cmp20::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(Cmp20::class.java).x++
    }
}

class IteratingMapperSystem1 : IteratingSystem(
    Family.all(Cmp1::class.java, Cmp2::class.java).exclude(Cmp3::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp1.mapper.get(entity).x++
    }
}

class IteratingMapperSystem2 : IteratingSystem(
    Family.all(Cmp3::class.java, Cmp4::class.java).exclude(Cmp5::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp3.mapper.get(entity).x++
    }
}

class IteratingMapperSystem3 : IteratingSystem(
    Family.all(Cmp5::class.java, Cmp6::class.java).exclude(Cmp7::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp5.mapper.get(entity).x++
    }
}

class IteratingMapperSystem4 : IteratingSystem(
    Family.all(Cmp8::class.java, Cmp9::class.java).exclude(Cmp10::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp8.mapper.get(entity).x++
    }
}

class IteratingMapperSystem5 : IteratingSystem(
    Family.all(Cmp11::class.java, Cmp12::class.java).exclude(Cmp13::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp11.mapper.get(entity).x++
    }
}

class IteratingMapperSystem6 : IteratingSystem(
    Family.all(Cmp14::class.java, Cmp15::class.java).exclude(Cmp16::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp14.mapper.get(entity).x++
    }
}

class IteratingMapperSystem7 : IteratingSystem(
    Family.all(Cmp17::class.java, Cmp18::class.java).exclude(Cmp19::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp17.mapper.get(entity).x++
    }
}

class IteratingMapperSystem8 : IteratingSystem(
    Family.one(Cmp1::class.java, Cmp10::class.java, Cmp20::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        Cmp20.mapper.get(entity).x++
    }
}

class IntervalSystem : IntervalSystem(1 / 60f) {
    private var counter = 0

    override fun updateInterval() {
        ++counter
    }
}

fun main() {
    benchmarkWithMappers()
    benchmarkWithoutMappers()
}

private fun benchmarkWithMappers() {
    val setupTimes = LongArray(NUM_TEST_RUNS)
    val executeTimes = LongArray(NUM_TEST_RUNS)

    for (i in 0 until NUM_TEST_RUNS) {
        lateinit var engine: PooledEngine
        setupTimes[i] = measureTimeMillis {
            engine = PooledEngine(
                NUM_ENTITIES,
                NUM_ENTITIES,
                NUM_COMPONENTS_PER_ENTITY,
                NUM_COMPONENTS_PER_ENTITY
            )
            engine.apply {
                addSystem(IteratingMapperSystem1())
                addSystem(IteratingMapperSystem2())
                addSystem(IteratingMapperSystem3())
                addSystem(IteratingMapperSystem4())
                addSystem(IteratingMapperSystem5())
                addSystem(IteratingMapperSystem6())
                addSystem(IteratingMapperSystem7())
                addSystem(IteratingMapperSystem8())
                addSystem(IntervalSystem())

                repeat(NUM_ENTITIES) {
                    addEntity(
                        createEntity().apply {
                            add(engine.createComponent(Cmp1::class.java))
                            add(engine.createComponent(Cmp2::class.java))
                            add(engine.createComponent(Cmp3::class.java))
                            add(engine.createComponent(Cmp4::class.java))
                            add(engine.createComponent(Cmp5::class.java))
                            add(engine.createComponent(Cmp6::class.java))
                            add(engine.createComponent(Cmp7::class.java))
                            add(engine.createComponent(Cmp8::class.java))
                            add(engine.createComponent(Cmp9::class.java))
                            add(engine.createComponent(Cmp10::class.java))
                            add(engine.createComponent(Cmp11::class.java))
                            add(engine.createComponent(Cmp12::class.java))
                            add(engine.createComponent(Cmp13::class.java))
                            add(engine.createComponent(Cmp14::class.java))
                            add(engine.createComponent(Cmp15::class.java))
                            add(engine.createComponent(Cmp16::class.java))
                            add(engine.createComponent(Cmp17::class.java))
                            add(engine.createComponent(Cmp18::class.java))
                            add(engine.createComponent(Cmp19::class.java))
                            add(engine.createComponent(Cmp20::class.java))
                        }
                    )
                }
            }
        }

        executeTimes[i] = measureTimeMillis {
            repeat(NUM_UPDATE_CALLS) {
                engine.update(1 / 60f)
            }
        }
    }

    println("ASHLEY BENCHMARK USING MAPPERS")
    println("Average setup time in ms: ${setupTimes.average()}")
    println("Average execution time in ms: ${executeTimes.average()}")
}

private fun benchmarkWithoutMappers() {
    val setupTimes = LongArray(NUM_TEST_RUNS)
    val executeTimes = LongArray(NUM_TEST_RUNS)

    for (i in 0 until NUM_TEST_RUNS) {
        lateinit var engine: PooledEngine
        setupTimes[i] = measureTimeMillis {
            engine = PooledEngine(
                NUM_ENTITIES,
                NUM_ENTITIES,
                NUM_COMPONENTS_PER_ENTITY,
                NUM_COMPONENTS_PER_ENTITY
            )
            engine.apply {
                addSystem(IteratingSystem1())
                addSystem(IteratingSystem2())
                addSystem(IteratingSystem3())
                addSystem(IteratingSystem4())
                addSystem(IteratingSystem5())
                addSystem(IteratingSystem6())
                addSystem(IteratingSystem7())
                addSystem(IteratingSystem8())
                addSystem(IntervalSystem())

                repeat(NUM_ENTITIES) {
                    addEntity(
                        createEntity().apply {
                            add(engine.createComponent(Cmp1::class.java))
                            add(engine.createComponent(Cmp2::class.java))
                            add(engine.createComponent(Cmp3::class.java))
                            add(engine.createComponent(Cmp4::class.java))
                            add(engine.createComponent(Cmp5::class.java))
                            add(engine.createComponent(Cmp6::class.java))
                            add(engine.createComponent(Cmp7::class.java))
                            add(engine.createComponent(Cmp8::class.java))
                            add(engine.createComponent(Cmp9::class.java))
                            add(engine.createComponent(Cmp10::class.java))
                            add(engine.createComponent(Cmp11::class.java))
                            add(engine.createComponent(Cmp12::class.java))
                            add(engine.createComponent(Cmp13::class.java))
                            add(engine.createComponent(Cmp14::class.java))
                            add(engine.createComponent(Cmp15::class.java))
                            add(engine.createComponent(Cmp16::class.java))
                            add(engine.createComponent(Cmp17::class.java))
                            add(engine.createComponent(Cmp18::class.java))
                            add(engine.createComponent(Cmp19::class.java))
                            add(engine.createComponent(Cmp20::class.java))
                        }
                    )
                }
            }
        }

        executeTimes[i] = measureTimeMillis {
            repeat(NUM_UPDATE_CALLS) {
                engine.update(1 / 60f)
            }
        }
    }

    println("ASHLEY BENCHMARK WITHOUT MAPPERS")
    println("Average setup time in ms: ${setupTimes.average()}")
    println("Average execution time in ms: ${executeTimes.average()}")
}
