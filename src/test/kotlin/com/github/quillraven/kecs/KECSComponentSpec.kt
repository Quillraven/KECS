package com.github.quillraven.kecs

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.RemoveComponent
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

@Suppress("UNUSED")
object KECSComponentSpec : Spek({
    describe("A ComponentManager") {
        val componentManager by memoized { KECSComponentManager(1, 2) }
        val entityManager by memoized { KECSEntityManager(componentManager, 1) }

        describe("creating a new component in an empty manager") {
            lateinit var component: KECSComponent
            beforeEachTest {
                component = componentManager.obtain<TransformComponent>()
            }

            it("should create a component of the given type") {
                (component is TransformComponent) `should be equal to` true
            }

            it("should create a new reflection pool for the component type") {
                componentManager.componentPools.containsKey(TransformComponent::class) `should be equal to` true
            }
        }

        describe("removing an existing component from a manager") {
            lateinit var component: TransformComponent
            beforeEachTest {
                component = componentManager.obtain<TransformComponent>().apply {
                    position.set(1f, 1f)
                }
                componentManager.free(component)
            }

            it("should reset the component") {
                component.position `should be equal to` Vector2(0f, 0f)
            }

            it("should add the component to the free objects of the component pool") {
                componentManager.componentPools[TransformComponent::class].free `should be equal to` 1
            }
        }

        describe("adding a non-existing component to an entity without mapper") {
            lateinit var component: TransformComponent
            lateinit var entity: KECSEntity
            beforeEachTest {
                component = componentManager.obtain()
                entity = entityManager.obtain()
                componentManager.add(entity, component)
            }

            it("should create a new mapper for the component type with id 0") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                mapper.type `should be equal to` TransformComponent::class
                mapper.id `should be equal to` 0
            }

            it("should add the component to the entity's components") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                componentManager.entityComponents[entity.id][mapper.id] `should be equal to` component
            }

            it("should set the component's bit to the entity's component bitset") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                componentManager.entityComponentBits[entity.id].get(mapper.id) `should be equal to` true
            }
        }

        describe("adding a component of the same type twice to an entity without mapper") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = entityManager.obtain()
                componentManager.add(entity, componentManager.obtain<TransformComponent>())
            }

            it("should throw a KECSComponentAlreadyExistingException") {
                invoking {
                    componentManager.add(
                        entity,
                        componentManager.obtain<TransformComponent>()
                    )
                } `should throw` KECSComponentAlreadyExistingException::class
            }
        }

        describe("adding a component to an entity that exceeds the current component size (=2) of the manager") {
            lateinit var transform: TransformComponent
            lateinit var remove: RemoveComponent
            lateinit var player: PlayerComponent
            lateinit var entity: KECSEntity
            beforeEachTest {
                transform = componentManager.obtain()
                remove = componentManager.obtain()
                player = componentManager.obtain()
                entity = entityManager.obtain()
                componentManager.add(entity, transform)
                componentManager.add(entity, remove)
                componentManager.add(entity, player)
            }

            it("should increase the maximum amount of possible components by 75% (=3)") {
                componentManager.entityComponents[entity.id].size `should be equal to` 3
            }

            it("should add all three components to the entity") {
                val mapperTransform = componentManager.componentMappers[TransformComponent::class]
                componentManager.entityComponents[entity.id][mapperTransform.id] `should be equal to` transform
                val mapperRemove = componentManager.componentMappers[RemoveComponent::class]
                componentManager.entityComponents[entity.id][mapperRemove.id] `should be equal to` remove
                val mapperPlayer = componentManager.componentMappers[PlayerComponent::class]
                componentManager.entityComponents[entity.id][mapperPlayer.id] `should be equal to` player
            }

            it("should set the components' bits to the entity's component bitset") {
                val mapperTransform = componentManager.componentMappers[TransformComponent::class]
                componentManager.entityComponentBits[entity.id].get(mapperTransform.id) `should be equal to` true
                val mapperRemove = componentManager.componentMappers[RemoveComponent::class]
                componentManager.entityComponentBits[entity.id].get(mapperRemove.id) `should be equal to` true
                val mapperPlayer = componentManager.componentMappers[PlayerComponent::class]
                componentManager.entityComponentBits[entity.id].get(mapperPlayer.id) `should be equal to` true
            }
        }

        describe("removing a component from an entity without mapper") {
            lateinit var entity: KECSEntity
            lateinit var component: TransformComponent
            beforeEachTest {
                entity = entityManager.obtain()
                component = componentManager.obtain()
                componentManager.add(entity, component)
                componentManager.remove(entity, component)
            }

            it("should create a new mapper for the component type with id 0") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                mapper.type `should be equal to` TransformComponent::class
                mapper.id `should be equal to` 0
            }

            it("should remove the component from the entity's components") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                componentManager.entityComponents[entity.id][mapper.id] `should be equal to` null
            }

            it("should reset the component") {
                component.position `should be equal to` Vector2(0f, 0f)
            }

            it("should clear the component's bit from the entity's component bitset") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                componentManager.entityComponentBits[entity.id].get(mapper.id) `should be equal to` false
            }
        }

        describe("getting a component from an entity without mapper") {
            lateinit var entity: KECSEntity
            lateinit var component: TransformComponent
            lateinit var retrievedComponent: TransformComponent
            beforeEachTest {
                entity = entityManager.obtain()
                component = componentManager.obtain()
                componentManager.add(entity, component)
                retrievedComponent = componentManager.get(entity)
            }

            it("should create a new mapper for the component type with id 0") {
                val mapper = componentManager.componentMappers[TransformComponent::class]
                mapper.type `should be equal to` TransformComponent::class
                mapper.id `should be equal to` 0
            }

            it("should return the existing component of the entity") {
                component `should be equal to` retrievedComponent
            }
        }

        describe("getting a component from an entity with mapper") {
            lateinit var entity: KECSEntity
            lateinit var component: TransformComponent
            lateinit var retrievedComponent: TransformComponent
            beforeEachTest {
                entity = entityManager.obtain()
                component = componentManager.obtain()
                componentManager.add(entity, component)
                val mapper = componentManager.mapper<TransformComponent>()
                retrievedComponent = componentManager.get(entity, mapper)
            }

            it("should return the existing component of the entity") {
                component `should be equal to` retrievedComponent
            }
        }

        describe("getting a non-existing component from an entity with mapper") {
            lateinit var entity: KECSEntity
            lateinit var mapper: KECSComponentMapper
            beforeEachTest {
                entity = entityManager.obtain()
                mapper = componentManager.mapper<TransformComponent>()
            }

            it("should throw a KECSMissingComponentException") {
                invoking { componentManager.get(entity, mapper) } `should throw` KECSMissingComponentException::class
            }
        }

        describe("getting a component bitset from an entity without components") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entityManager.addListener(componentManager)
                entity = entityManager.obtain()
            }

            it("should return an empty BitSet") {
                componentManager.entityComponentBits[entity.id] `should be equal to` BitSet()
            }
        }

        describe("removing an entity with two components") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            lateinit var playerComponent: PlayerComponent
            beforeEachTest {
                entityManager.addListener(componentManager)
                entity = entityManager.obtain()
                transformComponent = componentManager.obtain<TransformComponent>().apply {
                    position.set(1f, 1f)
                }
                playerComponent = componentManager.obtain()
                componentManager.add(entity, transformComponent)
                componentManager.add(entity, playerComponent)
                entityManager.free(entity)
            }

            it("should free the entity's components") {
                componentManager.entityComponents[entity.id].forEach { it `should be equal to` null }
                transformComponent.position `should be equal to` Vector2(0f, 0f)
            }

            it("should clear the component bits of the entity") {
                componentManager.entityComponentBits[entity.id] `should be equal to` BitSet()
            }
        }
    }
})
