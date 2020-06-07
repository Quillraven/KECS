package com.github.quillraven.kecs

import com.github.quillraven.kecs.system.TestInterpolationSystem
import com.github.quillraven.kecs.system.TestIntervalSystem
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("UNUSED")
object KECSSystemSpec : Spek({
    describe("A System Manager") {
        val intervalSystem by memoized { TestIntervalSystem() }
        val interpolationSystem by memoized { TestInterpolationSystem() }
        val systemManager by memoized {
            KECSSystemManager(
                intervalSystem,
                interpolationSystem
            )
        }

        describe("creating a new manager with two systems") {
            it("should store the systems in a collection") {
                systemManager.systems.size `should be equal to` 2
                systemManager.systems.contains(intervalSystem, true) `should be equal to` true
                systemManager.systems.contains(interpolationSystem, true) `should be equal to` true
            }
        }

        describe("updating a manager") {
            beforeEachTest {
                systemManager.update(1f)
            }

            it("should call update of each system") {
                intervalSystem.calls `should be equal to` 1
                interpolationSystem.intervalCalls `should be equal to` 1
            }
        }

        describe("updating an interval system with twice its rate") {
            beforeEachTest {
                intervalSystem.update(2f)
            }

            it("should call its interval function with its rate value") {
                intervalSystem.lastInterval `should be equal to` 1f
            }

            it("should call its interval function twice") {
                intervalSystem.calls `should be equal to` 2
            }
        }

        describe("updating an interpolation system with 2.5 times its rate") {
            beforeEachTest {
                interpolationSystem.update(2.5f)
            }

            it("should call its interval function with its rate value") {
                interpolationSystem.lastInterval `should be equal to` 1f
            }

            it("should call its interval function twice") {
                interpolationSystem.intervalCalls `should be equal to` 2
            }

            it("should call its interpolate function once") {
                interpolationSystem.interpolateCalls `should be equal to` 1
            }

            it("should call its interpolate function with alpha 0.5") {
                interpolationSystem.lastAlpha `should be equal to` 0.5f
            }
        }
    }
})
