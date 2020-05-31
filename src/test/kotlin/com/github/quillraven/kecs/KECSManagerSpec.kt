package com.github.quillraven.kecs

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("UNUSED")
object KECSManagerSpec : Spek({
    describe("An ECS manager") {
        val manager by memoized { KECSManager() }

        describe("adding an entity") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = manager.entity {
                    add<TransformComponent> {
                        position.set(1f, 1f)
                    }
                }
            }

            it("should add entity") {
                manager.entitySize `should be equal to` 1
            }

            it("should have configuration of init block") {
                (TransformComponent::class in entity.components) `should be equal to` true
                entity.get<TransformComponent>().position `should be equal to` Vector2(1f, 1f)
            }
        }

        describe("removing an entity") {
            lateinit var entity: KECSEntity

            beforeEachTest {
                entity = manager.entity {
                    add<TransformComponent>()
                }
                entity.free()
            }

            it("should remove entity") {
                manager.entitySize `should be equal to` 0
                manager.freeEntitySize `should be equal to` 1
            }

            it("should remove components of entity") {
                manager.freeComponentSize(TransformComponent::class) `should be equal to` 1
            }

            it("should reset entity") {
                entity.componentSize `should be equal to` 0
            }
        }
    }
})
