package com.github.quillraven.kecs.benchmark.kecs

import com.github.quillraven.kecs.KECSComponent
import com.github.quillraven.kecs.KECSComponentMapper
import com.github.quillraven.kecs.KECSEntity
import com.github.quillraven.kecs.KECSIntervalSystem
import com.github.quillraven.kecs.KECSIteratingSystem
import com.github.quillraven.kecs.KECSManager
import kotlin.system.measureTimeMillis

private const val NUM_ENTITIES = 1000
private const val NUM_COMPONENTS_PER_ENTITY = 20
private const val NUM_TEST_RUNS = 100
private const val NUM_UPDATE_CALLS = 3600

class Cmp1 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp2 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp3 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp4 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp5 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp6 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp7 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp8 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp9 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp10 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp11 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp12 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp13 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp14 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp15 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp16 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp17 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp18 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp19 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class Cmp20 : KECSComponent {
    var x = 0f

    override fun reset() {
        x = 0f
    }
}

class IteratingSystem1(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp1::class, Cmp2::class)
        noneOf(Cmp3::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp1::class].x++
    }
}

class IteratingSystem2(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp3::class, Cmp4::class)
        noneOf(Cmp5::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp3::class].x++
    }
}

class IteratingSystem3(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp5::class, Cmp6::class)
        noneOf(Cmp7::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp5::class].x++
    }
}

class IteratingSystem4(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp8::class, Cmp9::class)
        noneOf(Cmp10::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp8::class].x++
    }
}

class IteratingSystem5(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp11::class, Cmp12::class)
        noneOf(Cmp13::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp11::class].x++
    }
}

class IteratingSystem6(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp14::class, Cmp15::class)
        noneOf(Cmp16::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp14::class].x++
    }
}

class IteratingSystem7(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp17::class, Cmp18::class)
        noneOf(Cmp19::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp17::class].x++
    }
}

class IteratingSystem8(manager: KECSManager) : KECSIteratingSystem(
    manager.family {
        anyOf(Cmp1::class, Cmp10::class, Cmp20::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[Cmp20::class].x++
    }
}

class IteratingMapperSystem1(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp1> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp1::class, Cmp2::class)
        noneOf(Cmp3::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem2(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp3> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp3::class, Cmp4::class)
        noneOf(Cmp5::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem3(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp5> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp5::class, Cmp6::class)
        noneOf(Cmp7::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem4(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp8> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp8::class, Cmp9::class)
        noneOf(Cmp10::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem5(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp11> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp11::class, Cmp12::class)
        noneOf(Cmp13::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem6(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp14> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp14::class, Cmp15::class)
        noneOf(Cmp16::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem7(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp17> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        allOf(Cmp17::class, Cmp18::class)
        noneOf(Cmp19::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IteratingMapperSystem8(
    manager: KECSManager,
    private val cmpMapper: KECSComponentMapper<Cmp20> = manager.mapper()
) : KECSIteratingSystem(
    manager.family {
        anyOf(Cmp1::class, Cmp10::class, Cmp20::class)
    }
) {
    override fun updateEntity(entity: KECSEntity, deltaTime: Float) {
        entity[cmpMapper].x++
    }
}

class IntervalSystem : KECSIntervalSystem(1 / 60f) {
    private var counter = 0

    override fun interval(interval: Float) {
        counter++
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
        lateinit var manager: KECSManager
        setupTimes[i] = measureTimeMillis {
            manager = KECSManager(
                NUM_ENTITIES,
                NUM_COMPONENTS_PER_ENTITY
            )
            manager.apply {
                system(IteratingMapperSystem1(this))
                system(IteratingMapperSystem2(this))
                system(IteratingMapperSystem3(this))
                system(IteratingMapperSystem4(this))
                system(IteratingMapperSystem5(this))
                system(IteratingMapperSystem6(this))
                system(IteratingMapperSystem7(this))
                system(IteratingMapperSystem8(this))
                system(IntervalSystem())

                repeat(NUM_ENTITIES) {
                    manager.entity {
                        with<Cmp1>()
                        with<Cmp2>()
                        with<Cmp3>()
                        with<Cmp4>()
                        with<Cmp5>()
                        with<Cmp6>()
                        with<Cmp7>()
                        with<Cmp8>()
                        with<Cmp9>()
                        with<Cmp10>()
                        with<Cmp11>()
                        with<Cmp12>()
                        with<Cmp13>()
                        with<Cmp14>()
                        with<Cmp15>()
                        with<Cmp16>()
                        with<Cmp17>()
                        with<Cmp18>()
                        with<Cmp19>()
                        with<Cmp20>()
                    }
                }
            }
        }

        executeTimes[i] = measureTimeMillis {
            repeat(NUM_UPDATE_CALLS) {
                manager.update(1 / 60f)
            }
        }
    }

    println("KECS BENCHMARK USING MAPPERS")
    println("Average setup time in ms: ${setupTimes.average()}")
    println("Average execution time in ms: ${executeTimes.average()}")
}

private fun benchmarkWithoutMappers() {
    val setupTimes = LongArray(NUM_TEST_RUNS)
    val executeTimes = LongArray(NUM_TEST_RUNS)

    for (i in 0 until NUM_TEST_RUNS) {
        lateinit var manager: KECSManager
        setupTimes[i] = measureTimeMillis {
            manager = KECSManager(
                NUM_ENTITIES,
                NUM_COMPONENTS_PER_ENTITY
            )
            manager.apply {
                system(IteratingSystem1(this))
                system(IteratingSystem2(this))
                system(IteratingSystem3(this))
                system(IteratingSystem4(this))
                system(IteratingSystem5(this))
                system(IteratingSystem6(this))
                system(IteratingSystem7(this))
                system(IteratingSystem8(this))
                system(IntervalSystem())

                repeat(NUM_ENTITIES) {
                    manager.entity {
                        with<Cmp1>()
                        with<Cmp2>()
                        with<Cmp3>()
                        with<Cmp4>()
                        with<Cmp5>()
                        with<Cmp6>()
                        with<Cmp7>()
                        with<Cmp8>()
                        with<Cmp9>()
                        with<Cmp10>()
                        with<Cmp11>()
                        with<Cmp12>()
                        with<Cmp13>()
                        with<Cmp14>()
                        with<Cmp15>()
                        with<Cmp16>()
                        with<Cmp17>()
                        with<Cmp18>()
                        with<Cmp19>()
                        with<Cmp20>()
                    }
                }
            }
        }

        executeTimes[i] = measureTimeMillis {
            repeat(NUM_UPDATE_CALLS) {
                manager.update(1 / 60f)
            }
        }
    }

    println("KECS BENCHMARK WITHOUT MAPPERS")
    println("Average setup time in ms: ${setupTimes.average()}")
    println("Average execution time in ms: ${executeTimes.average()}")
}
