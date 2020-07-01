package com.github.quillraven.kecs

import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object WorldSpec : Spek({
    val world by memoized { World(2) }

    describe("A entity world") {
        describe("Adding an entity to an empty world") {
            var entityID = -1
            beforeEachTest {
                entityID = world.entity()
            }

            it("should create an entity with ID 0") {
                entityID `should be equal to` 0
            }

            it("should add the entity to the world") {
                (entityID in world) `should be equal to` true
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
            var entityID = -1
            beforeEachTest {
                entityID = world.entity()
                world.removeEntity(entityID)
            }

            it("should remove the entity from the world") {
                (entityID in world) `should be equal to` false
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

        describe("Adding a new component manager to an empty world") {
            lateinit var manager: ComponentManager<TransformComponent>
            beforeEachTest {
                manager = world.componentManager()
            }

            it("should create a manager for type TransformComponent") {
                manager `should not be equal to` null
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
                manager1 `should be equal to` manager2
            }
        }
    }
})