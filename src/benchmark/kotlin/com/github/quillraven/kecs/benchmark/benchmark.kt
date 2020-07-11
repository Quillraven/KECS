package com.github.quillraven.kecs.benchmark

import com.artemis.WorldConfigurationBuilder
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.quillraven.kecs.World
import com.github.quillraven.kecs.benchmark.artemis.ArtemisComponent1
import com.github.quillraven.kecs.benchmark.artemis.ArtemisComponent2
import com.github.quillraven.kecs.benchmark.artemis.ArtemisComponent3
import com.github.quillraven.kecs.benchmark.artemis.ArtemisIteratingSystemComplex1
import com.github.quillraven.kecs.benchmark.artemis.ArtemisIteratingSystemComplex2
import com.github.quillraven.kecs.benchmark.artemis.BenchmarkArtemis
import com.github.quillraven.kecs.benchmark.ashley.AshleyComponent1
import com.github.quillraven.kecs.benchmark.ashley.AshleyComponent2
import com.github.quillraven.kecs.benchmark.ashley.AshleyComponent3
import com.github.quillraven.kecs.benchmark.ashley.AshleyIteratingSystemComplex1
import com.github.quillraven.kecs.benchmark.ashley.AshleyIteratingSystemComplex2
import com.github.quillraven.kecs.benchmark.ashley.BenchmarkAshley
import com.github.quillraven.kecs.benchmark.kecs.BenchmarkKECS
import com.github.quillraven.kecs.benchmark.kecs.KECSComponent1
import com.github.quillraven.kecs.benchmark.kecs.KECSComponent2
import com.github.quillraven.kecs.benchmark.kecs.KECSComponent3
import com.github.quillraven.kecs.benchmark.kecs.KECSIteratingSystemComplex1
import com.github.quillraven.kecs.benchmark.kecs.KECSIteratingSystemComplex2
import kotlin.system.measureTimeMillis

abstract class Benchmark(
    val numEntities: Int = 10000,
    numWarmUps: Int = 3,
    numRuns: Int = 10,
    val simpleIterations: Int = 1000,
    val complexIterations: Int = 100
) {
    private val createTimes = LongArray(numRuns)
    val createTime: Double
        get() = createTimes.average()
    private val createAndRemoveTimes = LongArray(numRuns)
    val createAndRemoveTime: Double
        get() = createAndRemoveTimes.average()
    private val simpleTimes = LongArray(numRuns)
    val simpleTime: Double
        get() = simpleTimes.average()
    private val complexTimes = LongArray(numRuns)
    val complexTime: Double
        get() = complexTimes.average()

    init {
        repeat(numWarmUps) {
            warmUp()
        }

        for (i in 0 until numRuns) {
            createTimes[i] = measureTimeMillis {
                create()
            }
        }

        for (i in 0 until numRuns) {
            createAndRemoveTimes[i] = measureTimeMillis {
                createAndRemove()
            }
        }

        for (i in 0 until numRuns) {
            simpleTimes[i] = measureTimeMillis {
                simple()
            }
        }

        for (i in 0 until numRuns) {
            complexTimes[i] = measureTimeMillis {
                complex()
            }
        }
    }

    private fun warmUp() {
        create()
        createAndRemove()
        simple()
        complex()
    }

    abstract fun create()

    abstract fun createAndRemove()

    abstract fun simple()

    abstract fun complex()
}

fun main() {
    verifyBenchmarks()
    println(
        """
        Running Benchmarks...
        Result will be in ms
        KECS${'\t'}|${'\t'}Ashley${'\t'}|${'\t'}Artemis
    """.trimIndent()
    )

    BenchmarkAshley
    BenchmarkKECS
    BenchmarkArtemis

    println(
        """
        Create
        ${BenchmarkKECS.createTime}${'\t'}|${'\t'}${BenchmarkAshley.createTime}${'\t'}|${'\t'}${BenchmarkArtemis.createTime}
        
        Create and Remove
        ${BenchmarkKECS.createAndRemoveTime}${'\t'}|${'\t'}${BenchmarkAshley.createAndRemoveTime}${'\t'}|${'\t'}${BenchmarkArtemis.createAndRemoveTime}
        
        Simple Iterations
        ${BenchmarkKECS.simpleTime}${'\t'}|${'\t'}${BenchmarkAshley.simpleTime}${'\t'}|${'\t'}${BenchmarkArtemis.simpleTime}
        
        Complex Iterations
        ${BenchmarkKECS.complexTime}${'\t'}|${'\t'}${BenchmarkAshley.complexTime}${'\t'}|${'\t'}${BenchmarkArtemis.complexTime}
        
        --------------------
        
        KECS vs Ashley
        Create: ${BenchmarkKECS.createTime - BenchmarkAshley.createTime}
        Create and Remove: ${BenchmarkKECS.createAndRemoveTime - BenchmarkAshley.createAndRemoveTime}
        Simple Iterations: ${BenchmarkKECS.simpleTime - BenchmarkAshley.simpleTime}
        Complex Iterations: ${BenchmarkKECS.complexTime - BenchmarkAshley.complexTime}
        
        --------------------
                
        KECS vs Artemis
        Create: ${BenchmarkKECS.createTime - BenchmarkArtemis.createTime}
        Create and Remove: ${BenchmarkKECS.createAndRemoveTime - BenchmarkArtemis.createAndRemoveTime}
        Simple Iterations: ${BenchmarkKECS.simpleTime - BenchmarkArtemis.simpleTime}
        Complex Iterations: ${BenchmarkKECS.complexTime - BenchmarkArtemis.complexTime}
    """.trimIndent()
    )
}

fun verifyBenchmarks() {
    val numEntities = 100
    val numIterations = 10

    // check that complex benchmark has the same result for all three systems
    val engine = setupAshleyVerification(numEntities)
    val kecsWorld = setupKECSVerification(numEntities)
    val artemisWorld = setupArtemisVerification(numEntities)
    repeat(numIterations) {
        engine.update(1f)
        kecsWorld.update(1f)
        artemisWorld.setDelta(1f)
        artemisWorld.process()
    }

    println(
        "Verification KECS: ${
        kecsWorld.componentManager<KECSComponent1>()[0].x == numIterations
                && (0 !in kecsWorld.componentManager<KECSComponent2>())
                && kecsWorld.componentManager<KECSComponent3>()[0].counter == numIterations
                && kecsWorld.componentManager<KECSComponent1>()[1].x == 0
                && (1 !in kecsWorld.componentManager<KECSComponent2>())
                && kecsWorld.componentManager<KECSComponent3>()[1].counter == numIterations
        }"
    )
    println(
        "Verification Ashley: ${
        engine.entities[0].getComponent(AshleyComponent1::class.java).x == 0
                && engine.entities[0].getComponent(AshleyComponent2::class.java) == null
                && engine.entities[0].getComponent(AshleyComponent3::class.java).counter == numIterations
                && engine.entities[1].getComponent(AshleyComponent1::class.java).x == 0
                && engine.entities[1].getComponent(AshleyComponent2::class.java) == null
                && engine.entities[1].getComponent(AshleyComponent3::class.java).counter == numIterations
        }"
    )
    println(
        "Verification Artemis: ${
        artemisWorld.getEntity(0).getComponent(ArtemisComponent1::class.java).x == numIterations
                && artemisWorld.getEntity(0).getComponent(ArtemisComponent2::class.java) == null
                && artemisWorld.getEntity(0).getComponent(ArtemisComponent3::class.java).counter == numIterations
                && artemisWorld.getEntity(1).getComponent(ArtemisComponent1::class.java).x == 0
                && artemisWorld.getEntity(1).getComponent(ArtemisComponent2::class.java) == null
                && artemisWorld.getEntity(1).getComponent(ArtemisComponent3::class.java).counter == numIterations
        }"
    )
}

fun setupArtemisVerification(numEntities: Int): com.artemis.World {
    val world = com.artemis.World(WorldConfigurationBuilder().run {
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
    return world
}

fun setupKECSVerification(numEntities: Int): World {
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
    return world
}

fun setupAshleyVerification(numEntities: Int): Engine {
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
    return engine
}
