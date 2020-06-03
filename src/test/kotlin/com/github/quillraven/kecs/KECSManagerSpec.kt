package com.github.quillraven.kecs

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.kecs.component.RemoveComponent
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("UNUSED")
object KECSManagerSpec : Spek({
    describe("A KECS Manager") {
        val manager by memoized { KECSManager() }

        describe("Creating an entity without components") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = manager.entity()
            }

            it("should create a new entity with id 0") {
                entity.id `should be equal to` 0
            }

            it("should create a new active entity") {
                entity.active `should be equal to` true
            }

            it("should create a new entity without components") {
                manager.componentManager.entityComponents[entity.id].forEach {
                    it `should be equal to` null
                }
            }

            it("should add the entity to the entity manager") {
                (entity in manager.entityManager) `should be equal to` true
            }
        }

        describe("Creating an entity with two components using and init block for one component") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            lateinit var removeComponent: RemoveComponent
            beforeEachTest {
                entity = manager.entity {
                    transformComponent = add {
                        position.set(1f, 1f)
                    }
                    removeComponent = add()
                }
            }

            it("should add two components to the entity") {
                (transformComponent in entity) `should be equal to` true
                (removeComponent in entity) `should be equal to` true
            }

            it("should set the values of one component to the init block properties") {
                transformComponent.position `should be equal to` Vector2(1f, 1f)
            }
        }
    }
})
