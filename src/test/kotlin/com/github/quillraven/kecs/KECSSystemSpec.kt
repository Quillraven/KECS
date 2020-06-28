package com.github.quillraven.kecs

import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.TransformComponent
import com.github.quillraven.kecs.system.TestInterpolationSystem
import com.github.quillraven.kecs.system.TestIntervalSystem
import com.github.quillraven.kecs.system.TestIteratingSystem
import com.github.quillraven.kecs.system.TestSortIteratingSystem
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("UNUSED")
object KECSSystemSpec : Spek({
    describe("A System Manager") {
        val manager by memoized { KECSManager() }
        val intervalSystem by memoized { TestIntervalSystem() }
        val interpolationSystem by memoized { TestInterpolationSystem() }
        val iteratingSystem by memoized { TestIteratingSystem(manager) }
        val sortIteratingSystem by memoized { TestSortIteratingSystem(manager) }
        val systemManager by memoized {
            KECSSystemManager(
                intervalSystem,
                interpolationSystem,
                iteratingSystem,
                sortIteratingSystem
            )
        }

        describe("creating a new manager with two systems") {
            it("should store the systems in a collection") {
                systemManager.systems.size `should be equal to` 4
                systemManager.systems.contains(intervalSystem, true) `should be equal to` true
                systemManager.systems.contains(interpolationSystem, true) `should be equal to` true
                systemManager.systems.contains(iteratingSystem, true) `should be equal to` true
                systemManager.systems.contains(sortIteratingSystem, true) `should be equal to` true
            }

            it("should initialize each system as active system") {
                intervalSystem.active `should be equal to` true
                interpolationSystem.active `should be equal to` true
                iteratingSystem.active `should be equal to` true
                sortIteratingSystem.active `should be equal to` true
            }
        }

        describe("updating a manager") {
            beforeEachTest {
                interpolationSystem.active = false
                systemManager.update(1f)
            }

            it("should call update of each active system") {
                intervalSystem.calls `should be equal to` 1
                interpolationSystem.intervalCalls `should be equal to` 0
                iteratingSystem.updateCalls `should be equal to` 1
                sortIteratingSystem.updateCalls `should be equal to` 1
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

        describe("updating an iterating system") {
            lateinit var entity1: KECSEntity
            lateinit var entity2: KECSEntity
            beforeEachTest {
                // make a call to iteratingSystem first to create it and its family
                iteratingSystem.update(0f)
                // after the family is created, we can add the entities
                manager.run {
                    entity1 = entity {
                        with<PlayerComponent>()
                    }
                    entity2 = entity {
                        with<PlayerComponent>()
                    }
                }
                iteratingSystem.update(0f)
            }

            it("should call its updateEntity function per entity of the family") {
                iteratingSystem.updateEntityCalls `should be equal to` 2
                entity1[PlayerComponent::class].updateCalls `should be equal to` 1
                entity2[PlayerComponent::class].updateCalls `should be equal to` 1
            }
        }

        describe("sorting an iterating system") {
            lateinit var entity1: KECSEntity
            lateinit var entity2: KECSEntity
            lateinit var entity3: KECSEntity
            beforeEachTest {
                // make a call to iteratingSystem first to create it and its family
                sortIteratingSystem.update(0f)
                // after the family is created, we can add the entities
                manager.run {
                    entity1 = entity {
                        with<PlayerComponent>()
                        with<TransformComponent> {
                            position.y = 3f
                        }
                    }
                    entity2 = entity {
                        with<PlayerComponent>()
                        with<TransformComponent> {
                            position.y = 2f
                        }
                    }
                    entity3 = entity {
                        with<PlayerComponent>()
                        with<TransformComponent> {
                            position.y = 1f
                        }
                    }
                }
                sortIteratingSystem.update(0f)
            }

            it("should sort the entities of the related family") {
                sortIteratingSystem.entities.orderedItems()[0] `should be equal to` entity3
                sortIteratingSystem.entities.orderedItems()[1] `should be equal to` entity2
                sortIteratingSystem.entities.orderedItems()[2] `should be equal to` entity1
            }

            it("should call updateEntity in the sorted order") {
                entity1[TransformComponent::class].position.x `should be equal to` 2f
                entity2[TransformComponent::class].position.x `should be equal to` 1f
                entity3[TransformComponent::class].position.x `should be equal to` 0f
            }
        }
    }
})
