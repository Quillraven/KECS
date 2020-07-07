package com.github.quillraven.kecs

import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.TransformComponent
import com.github.quillraven.kecs.system.TestEntityListener
import com.github.quillraven.kecs.system.TestIteratingSystem
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object WorldSpec : Spek({
    val world by memoized { World(2) }

    describe("A entity world") {
        describe("Adding an entity to an empty world") {
            var entityID = -1
            lateinit var listener: TestEntityListener
            beforeEachTest {
                listener = TestEntityListener()
                world.addListener(listener)
                entityID = world.entity()
            }

            it("should create an entity with ID 0") {
                entityID `should be equal to` 0
            }

            it("should add the entity to the world") {
                (entityID in world) `should be equal to` true
            }

            it("should call entityAdded for all entity listeners") {
                listener.addCalls `should be equal to` 1
            }

            it("should not call entityRemoved for all entity listeners") {
                listener.removeCalls `should be equal to` 0
            }
        }

        describe("Checking if a non-existing entity exists") {
            it("should return false") {
                (0 in world) `should be equal to` false
            }
        }

        describe("Adding an entity to a non-empty world") {
            var entityID = -1
            beforeEachTest {
                world.entity()
                entityID = world.entity()
            }

            it("should create an entity with ID 1") {
                entityID `should be equal to` 1
            }

            it("should add the entity to the world") {
                (entityID in world) `should be equal to` true
            }
        }

        describe("Removing an existing entity") {
            lateinit var listener: TestEntityListener
            var entityID = -1
            beforeEachTest {
                entityID = world.entity()
                listener = TestEntityListener()
                world.addListener(listener)
                world.removeEntity(entityID)
            }

            it("should remove the entity from the world") {
                (entityID in world) `should be equal to` false
            }

            it("should call entityRemoved for all entity listeners") {
                listener.removeCalls `should be equal to` 1
            }

            it("should not call entityAdded for all entity listeners") {
                listener.addCalls `should be equal to` 0
            }
        }

        describe("Removing an existing and adding new entities") {
            var removedID = -1
            var recycledID = -1
            var newID = -1
            beforeEachTest {
                removedID = world.entity()
                world.removeEntity(removedID)
                recycledID = world.entity()
                newID = world.entity()
            }

            it("should reuse the ID of the removed entity") {
                removedID `should be equal to` recycledID
                removedID `should be equal to` 0
            }

            it("should create a new ID if there are no recycled IDs left") {
                newID `should not be equal to` recycledID
                newID `should be equal to` 1
            }

            it("should add the entities to the world") {
                (recycledID in world) `should be equal to` true
                (newID in world) `should be equal to` true
            }
        }

        describe("Adding an entity that exceeds the initial capacity") {
            var entityID = -1
            beforeEachTest {
                world.entity()
                world.entity()
                entityID = world.entity()
            }

            it("should resize the capacity by 75%") {
                world.size `should be equal to` 3
            }

            it("should create an entity of ID 2") {
                entityID `should be equal to` 2
            }
        }

        describe("Adding a new component manager to an empty world") {
            lateinit var manager: ComponentManager<TransformComponent>
            beforeEachTest {
                manager = world.componentManager()
            }

            it("should create a manager for type TransformComponent") {
                manager `should not be equal to` null
                manager.type `should be equal to` TransformComponent::class.java
            }

            it("should add the manager as EntityListener to the world") {
                (manager in world) `should be equal to` true
            }
        }

        describe("Adding a component manager of the same type twice") {
            lateinit var manager1: ComponentManager<TransformComponent>
            lateinit var manager2: ComponentManager<TransformComponent>
            beforeEachTest {
                manager1 = world.componentManager()
                manager2 = world.componentManager()
            }

            it("should create a manager only once") {
                manager1 `should not be equal to` null
                manager1 `should be` manager2
            }
        }

        describe("Adding a component manager to a non-empty world") {
            lateinit var manager1: ComponentManager<TransformComponent>
            lateinit var manager2: ComponentManager<PlayerComponent>
            beforeEachTest {
                manager1 = world.componentManager()
                manager2 = world.componentManager()
            }

            it("should create a manager for type TransformComponent") {
                manager1 `should not be equal to` null
                manager1.type `should be equal to` TransformComponent::class.java
            }

            it("should create a manager for type PlayerComponent") {
                manager2 `should not be equal to` null
                manager2.type `should be equal to` PlayerComponent::class.java
            }

            it("should add both managers as EntityListener to the world") {
                (manager1 in world) `should be equal to` true
                (manager2 in world) `should be equal to` true
            }
        }

        describe("Adding an entity listener") {
            lateinit var listener: EntityListener
            beforeEachTest {
                listener = TestEntityListener()
                world.addListener(listener)
            }

            it("should add the listener to the world") {
                (listener in world) `should be equal to` true
            }
        }

        describe("Removing an entity listener") {
            lateinit var listener: EntityListener
            beforeEachTest {
                listener = TestEntityListener()
                world.addListener(listener)
                world.removeListener(listener)
            }

            it("should remove the listener from the world") {
                (listener in world) `should be equal to` false
            }
        }

        describe("Adding an entity listener twice") {
            lateinit var listener: TestEntityListener
            beforeEachTest {
                listener = TestEntityListener()
                world.addListener(listener)
                world.addListener(listener)
                world.entity()
            }

            it("should add the listener only once") {
                listener.addCalls `should be equal to` 1
            }
        }

        describe("Creating a family") {
            lateinit var family: Family
            beforeEachTest {
                family = world.family { }
            }

            it("should add the family to the families cache") {
                (family in world) `should be equal to` true
            }
        }

        describe("Creating the same family twice") {
            lateinit var family1: Family
            lateinit var family2: Family
            beforeEachTest {
                family1 = world.family { }
                family2 = world.family { }
            }

            it("should create the family only once") {
                family1 `should be` family2
            }
        }

        describe("Adding a system to the world") {
            lateinit var system: IteratingSystem
            beforeEachTest {
                system = TestIteratingSystem(world)
                world.systems(system)
            }

            it("should add the system to the world") {
                (system in world) `should be equal to` true
            }
        }

        describe("Calling update of a world with systems") {
            lateinit var system1: TestIteratingSystem
            lateinit var system2: TestIteratingSystem
            beforeEachTest {
                system1 = TestIteratingSystem(world)
                system2 = TestIteratingSystem(world).apply {
                    active = false
                }
                world.systems(system1, system2)
                world.update(1 / 60f)
            }

            it("should call update of each active system") {
                system1.updateCalls `should be equal to` 1
                system2.updateCalls `should be equal to` 0
            }
        }
    }
})
