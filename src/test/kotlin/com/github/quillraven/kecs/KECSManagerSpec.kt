package com.github.quillraven.kecs

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.kecs.component.PhysicComponent
import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.RemoveComponent
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

@Suppress("UNUSED")
object KECSManagerSpec : Spek({
    describe("A KECS Manager") {
        val manager by memoized { KECSManager(1) }

        describe("creating an entity without components") {
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
                entity.components.forEach { it `should be equal to` null }
            }

            it("should add the entity to the entity manager") {
                (entity in manager) `should be equal to` true
            }
        }

        describe("creating an entity with two components using an init block for one component") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            lateinit var removeComponent: RemoveComponent
            beforeEachTest {
                entity = manager.entity {
                    transformComponent = with {
                        position.set(1f, 1f)
                    }
                    removeComponent = with()
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

        describe("creating two families using an init block once") {
            lateinit var family1: KECSFamily
            lateinit var family2: KECSFamily
            beforeEachTest {
                family1 = manager.family {
                    allOf(PhysicComponent::class, PlayerComponent::class)
                    noneOf(RemoveComponent::class)
                    anyOf(TransformComponent::class)
                }
                family2 = manager.family {}
            }

            it("should create a family with the properties of the init block") {
                family1.allSet.cardinality() `should be equal to` 2
                family1.noneSet.cardinality() `should be equal to` 1
                family1.anySet.cardinality() `should be equal to` 1
            }

            it("should create an empty family") {
                family2.allSet.cardinality() `should be equal to` 0
                family2.noneSet.cardinality() `should be equal to` 0
                family2.anySet.cardinality() `should be equal to` 0
            }
        }

        describe("getting components from an entity without components") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = manager.entity()
            }

            it("should return an Array of nulls for components") {
                entity.components.forEach { it `should be equal to` null }
            }

            it("should return an empty BitSet for the entity's bitset") {
                entity.componentBits `should be equal to` BitSet()
            }
        }

        describe("getting components from an entity that exceeded the initial entity capacity (=1) of the manager") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            lateinit var removeComponent: RemoveComponent
            beforeEachTest {
                manager.entity()
                entity = manager.entity {
                    transformComponent = with {
                        position.set(1f, 1f)
                    }
                    removeComponent = with()
                }
            }

            it("should add the components to the entity") {
                (transformComponent in entity) `should be equal to` true
                (removeComponent in entity) `should be equal to` true
            }
        }

        describe("getting components from an entity without components that exceeded the initial entity capacity (=1) of the manager") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                manager.entity()
                entity = manager.entity()
            }

            it("should return an Array of nulls for components") {
                entity.components.forEach { it `should be equal to` null }
            }

            it("should return an empty BitSet for the entity's bitset") {
                entity.componentBits `should be equal to` BitSet()
            }
        }

        describe("removing an entity with two components") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            beforeEachTest {
                entity = manager.entity {
                    transformComponent = with {
                        position.set(1f, 1f)
                    }
                    with<PlayerComponent>()
                }
                entity.free()
            }

            it("should free the entity's components") {
                entity.components.forEach { it `should be equal to` null }
                transformComponent.position `should be equal to` Vector2(0f, 0f)
            }

            it("should clear the component bits of the entity") {
                entity.componentBits `should be equal to` BitSet()
            }
        }

        describe("creating a component mapper") {
            lateinit var mapper: KECSComponentMapper<TransformComponent>
            beforeEachTest {
                mapper = manager.mapper()
            }

            it("should return a new component mapper of the given type") {
                mapper.type `should be equal to` TransformComponent::class
            }
        }
    }
})
