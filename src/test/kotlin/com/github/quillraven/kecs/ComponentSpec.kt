package com.github.quillraven.kecs

import com.github.quillraven.kecs.component.TestComponentListener
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object ComponentSpec : Spek({
    val entityCapacity = 2
    val listener by memoized { TestComponentListener() }
    val manager by memoized {
        ComponentManager(entityCapacity, TransformComponent::class.java).apply {
            addListener(listener)
        }
    }

    describe("A component manager") {
        describe("Registering an entity to an empty manager") {
            lateinit var component: TransformComponent
            val entityID = 0
            beforeEachTest {
                manager.register(entityID)
                component = manager[entityID]
            }

            it("should create a new component instance") {
                (entityID in manager) `should be equal to` true
                component `should be instance of` TransformComponent::class
            }

            it("should call componentAdded for all listeners") {
                listener.addCalls `should be equal to` 1
            }

            it("should not call componentRemoved for any listener") {
                listener.removeCalls `should be equal to` 0
            }
        }

        describe("Deregistering an entity from a manager") {
            val entityID = 0
            beforeEachTest {
                manager.removeListener(listener)
                manager.register(entityID)
                manager.addListener(listener)
                manager.deregister(entityID)
            }

            it("should remove the entity's component data") {
                (entityID in manager) `should be equal to` false
            }

            it("should throw an exception when accessing the data") {
                invoking { manager[entityID] } `should throw` MissingComponentException::class
            }

            it("should call componentRemoved for all listeners") {
                listener.removeCalls `should be equal to` 1
            }

            it("should not call componentAdded for any listener") {
                listener.addCalls `should be equal to` 0
            }
        }

        describe("Adding a ComponentListener to a manager and adding and removing an entity") {
            lateinit var listener2: TestComponentListener
            beforeEachTest {
                listener2 = TestComponentListener()
                manager.addListener(listener2)
                manager.register(0)
                manager.deregister(0)
            }

            it("should call componentAdded for the listener") {
                listener2.addCalls `should be equal to` 1
            }

            it("should call componentRemoved for the listener") {
                listener2.removeCalls `should be equal to` 1
            }

            it("should add the listener to the listeners list") {
                (listener2 in manager) `should be equal to` true
            }
        }

        describe("Removing a ComponentListener from a manager and adding and removing an entity") {
            lateinit var listener2: TestComponentListener
            beforeEachTest {
                listener2 = TestComponentListener()
                manager.addListener(listener2)
                manager.removeListener(listener2)
                manager.register(0)
                manager.deregister(0)
            }

            it("should not call componentAdded for the listener") {
                listener2.addCalls `should be equal to` 0
            }

            it("should not call componentRemoved for the listener") {
                listener2.removeCalls `should be equal to` 0
            }

            it("should remove the listener from the listeners list") {
                (listener2 in manager) `should be equal to` false
            }
        }

        describe("Removing an entity from the world") {
            val entityID = 0
            beforeEachTest {
                manager.removeListener(listener)
                manager.register(entityID)
                manager.addListener(listener)
                manager.entityRemoved(entityID)
            }

            it("should remove the entity's component data") {
                (entityID in manager) `should be equal to` false
            }

            it("should throw an exception when accessing the data") {
                invoking { manager[entityID] } `should throw` MissingComponentException::class
            }

            it("should call componentRemoved for all listeners") {
                listener.removeCalls `should be equal to` 1
            }

            it("should not call componentAdded for any listener") {
                listener.addCalls `should be equal to` 0
            }
        }

        describe("Registering the same entity twice") {
            val entityID = 0
            lateinit var component1: TransformComponent
            lateinit var component2: TransformComponent
            beforeEachTest {
                component1 = manager.register(entityID)
                component2 = manager.register(entityID)
            }

            it("should return the same component") {
                component1 `should be` component2
            }

            it("should call componentAdded only once") {
                listener.addCalls `should be equal to` 1
            }
        }

        describe("Deregistering a non-existing entity") {
            beforeEachTest {
                manager.deregister(0)
            }

            it("should not call componentRemoved for any listener") {
                listener.removeCalls `should be equal to` 0
            }
        }

        describe("Removing an entity and adding a new one") {
            val entityID = 0
            lateinit var component1: TransformComponent
            lateinit var component2: TransformComponent
            beforeEachTest {
                component1 = manager.register(entityID)
                manager.deregister(entityID)
                component2 = manager.register(entityID)
            }

            it("should reuse a single component instance") {
                component1 `should be` component2
            }
        }

        describe("Adding an entity that exceeds the manager's initial capacity") {
            beforeEachTest {
                manager.register(entityCapacity)
            }

            it("should resize the capacity by 75%") {
                manager.size `should be equal to` (entityCapacity * 1.75).toInt()
            }
        }
    }
})
