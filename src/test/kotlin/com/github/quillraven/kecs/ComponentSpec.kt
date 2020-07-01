package com.github.quillraven.kecs

import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object ComponentSpec : Spek({
    val world by memoized { World(2) }

    describe("A component manager") {
        describe("Registering an entity to an empty manager") {
            lateinit var manager: ComponentManager<TransformComponent>
            lateinit var component: TransformComponent
            var entityID = -1
            beforeEachTest {
                manager = world.componentManager()
                entityID = world.entity()
                manager.register(entityID)
                component = manager[entityID]
            }

            it("should create a new component instance") {
                (entityID in manager) `should be equal to` true
                component `should be instance of` TransformComponent::class
            }
        }

        describe("Unregistering an entity from a manager") {
            lateinit var manager: ComponentManager<TransformComponent>
            var entityID = -1
            beforeEachTest {
                manager = world.componentManager()
                entityID = world.entity()
                manager.register(entityID)
                manager.unregister(entityID)
            }

            it("should remove the entity's component data") {
                (entityID in manager) `should be equal to` false
            }

            it("should throw an exception when accessing the data") {
                invoking { manager[entityID].x } `should throw` KotlinNullPointerException::class
            }
        }
    }
})