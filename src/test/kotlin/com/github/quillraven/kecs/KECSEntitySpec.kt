package com.github.quillraven.kecs

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.github.quillraven.kecs.component.PhysicComponent
import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("UNUSED")
object KECSEntitySpec : Spek({
    describe("An EntityManager") {
        val componentManager by memoized { KECSComponentManager(2, 2) }
        val entityManager by memoized { KECSEntityManager(componentManager, 2) }

        describe("creating a new entity in an empty manager") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = entityManager.obtain()
            }

            it("should create an entity with id 0") {
                entity.id `should be equal to` 0
            }

            it("should create an active entity") {
                entity.active `should be equal to` true
            }

            it("should store the entity in the manager") {
                (entity in entityManager) `should be equal to` true
            }

            it("should increase the entity pool's next id") {
                entityManager.entityPool.nextId `should be equal to` 1
            }
        }

        describe("removing an existing entity from a manager") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = entityManager.obtain()
                entityManager.free(entity)
            }

            it("should set the active flag of the entity to false") {
                entity.active `should be equal to` false
            }

            it("should remove the entity from the manager") {
                (entity in entityManager) `should be equal to` false
            }

            it("should add the entity to the free objects of the entity pool") {
                entityManager.entityPool.free `should be equal to` 1
            }
        }

        describe("creating a new entity that exceeds the current entity size (=2) of the manager") {
            val entities = Array<KECSEntity>()
            beforeEachTest {
                entities.clear()
                repeat(3) {
                    entities.add(entityManager.obtain())
                }
            }

            it("should increase the maximum amount of possible entities by 75% (=3)") {
                entityManager.entities.size `should be equal to` 3
            }

            it("should contain all three entities") {
                entities.forEach { (it in entityManager) `should be equal to` true }
            }
        }

        describe("creating a new entity with a non-empty entity pool") {
            lateinit var entity: KECSEntity
            var recycleId = -1
            var nextId = -1
            beforeEachTest {
                entity = entityManager.obtain()
                entityManager.obtain()
                recycleId = entity.id
                nextId = entityManager.entityPool.nextId
                entityManager.free(entity)
                entity = entityManager.obtain()
            }

            it("should reuse an existing entity from the entity pool") {
                entity.id `should be equal to` recycleId
            }

            it("should set the active flag of the entity to true") {
                entity.active `should be equal to` true
            }

            it("should add the entity to the manager") {
                entityManager.entities.size `should be equal to` 2
                (entity in entityManager) `should be equal to` true
            }

            it("should not touch the entity's pool nextId") {
                entityManager.entityPool.nextId `should be equal to` nextId
            }
        }

        describe("updating the components of an entity by adding two components using an init block once") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            lateinit var playerComponent: PlayerComponent
            beforeEachTest {
                entity = entityManager.obtain()
                val mapper = componentManager.mapper<PlayerComponent>()
                entity.update {
                    playerComponent = add(mapper)
                    transformComponent = add {
                        position.set(1f, 1f)
                    }
                }
            }

            it("should add the component and set the init block properties") {
                (transformComponent in entity) `should be equal to` true
                transformComponent.position `should be equal to` Vector2(1f, 1f)
            }

            it("should add the component without the init block") {
                (playerComponent in entity) `should be equal to` true
            }
        }

        describe("updating the components of an entity by removing existing components") {
            lateinit var entity: KECSEntity
            lateinit var transformComponent: TransformComponent
            lateinit var playerComponent: PlayerComponent
            lateinit var physiqueComponent: PhysicComponent
            beforeEachTest {
                entity = entityManager.obtain()
                entity.update {
                    transformComponent = add {
                        position.set(1f, 1f)
                    }
                    remove<TransformComponent>()
                    playerComponent = add()
                    remove(playerComponent)
                    physiqueComponent = add()
                    val mapper = componentManager.mapper<PhysicComponent>()
                    remove<PhysicComponent>(mapper)
                }
            }

            it("should remove the transform component") {
                (transformComponent in entity) `should be equal to` false
            }

            it("should reset the transform component") {
                transformComponent.position `should be equal to` Vector2(0f, 0f)
            }

            it("should remove the player component") {
                (playerComponent in entity) `should be equal to` false
            }

            it("should remove the physique component") {
                (physiqueComponent in entity) `should be equal to` false
            }
        }
    }
})
