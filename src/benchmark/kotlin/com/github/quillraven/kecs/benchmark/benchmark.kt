package com.github.quillraven.kecs.benchmark

import com.github.quillraven.kecs.benchmark.artemis.BenchmarkArtemis
import com.github.quillraven.kecs.benchmark.ashley.BenchmarkAshley
import com.github.quillraven.kecs.benchmark.kecs.BenchmarkKECS
import kotlin.system.measureTimeMillis

abstract class Benchmark(
    val numEntities: Int = 10000,
    private val numWarmUps: Int = 3,
    private val numRuns: Int = 1,
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

    fun run() {
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
                complex(false)
            }
        }
    }

    fun warmUp() {
        create()
        createAndRemove()
        simple()
        complex(false)
    }

    abstract fun create()

    abstract fun createAndRemove()

    abstract fun simple()

    abstract fun complex(verify: Boolean): Boolean
}

fun main() {
    BenchmarkKECS.warmUp()
    BenchmarkAshley.warmUp()
    BenchmarkArtemis.warmUp()
    println("Verification KECS: ${BenchmarkKECS.complex(true)}")
    println("Verification Ashley: ${BenchmarkAshley.complex(true)}")
    println("Verification Artemis: ${BenchmarkArtemis.complex(true)}")

    println(
        """
        Running Benchmarks...
        Result will be in ms
        KECS${'\t'}|${'\t'}Ashley${'\t'}|${'\t'}Artemis
    """.trimIndent()
    )

    BenchmarkKECS.run()
    BenchmarkAshley.run()
    BenchmarkArtemis.run()

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
