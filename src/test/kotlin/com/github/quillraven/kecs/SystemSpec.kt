package com.github.quillraven.kecs

import com.github.quillraven.kecs.component.TransformComponent
import com.github.quillraven.kecs.system.TestIteratingSystem
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object SystemSpec : Spek({
    val world by memoized { World(2) }
    val iteratingSystem by memoized { TestIteratingSystem(world) }

    describe("An IteratingSystem") {
        describe("Updating an IteratingSystem") {
            var entity1 = -1
            var entity2 = -1
            beforeEachTest {
                entity1 = world.entity()
                entity2 = world.entity()
                world.systems(iteratingSystem)
                world.componentManager<TransformComponent>().register(entity1)
                world.componentManager<TransformComponent>().register(entity2)
                iteratingSystem.update(world, 1f)
            }

            it("should call updateEntity for each entity in the system") {
                world.componentManager<TransformComponent>()[entity1].x `should be equal to` 1
                world.componentManager<TransformComponent>()[entity2].x `should be equal to` 2
                iteratingSystem.updateEntityCalls `should be equal to` 2
            }

            it("should call updateEntity with the deltaTime of update") {
                iteratingSystem.lastUpdateEntityDeltaTime `should be equal to` 1f
            }
        }
    }
})
