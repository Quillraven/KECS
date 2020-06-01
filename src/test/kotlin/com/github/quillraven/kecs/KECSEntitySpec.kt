package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("UNUSED")
object KECSEntitySpec : Spek({
    describe("An EntityManager") {
        val manager by memoized { KECSEntityManager(2) }

        describe("creating a new entity in an empty manager") {
            lateinit var entity: KECSEntity
            beforeEachTest {
                entity = manager.obtain()
            }

            it("should create an entity with id 0") {
                entity.id `should be equal to` 0
            }

            it("should create an active entity") {
                entity.active `should be equal to` true
            }

            it("should store the entity in the manager") {
                (entity in manager) `should be equal to` true
            }

            it("should increase the entity pool's next id") {
                manager.entityPool.nextId `should be equal to` 1
            }
        }

        describe("removing an existing entity from a manager") {
            lateinit var entity: KECSEntity
            var entityId: Int = -1
            beforeEachTest {
                entity = manager.obtain()
                entityId = entity.id
                manager.free(entity)
            }

            it("should set the entity's id to -1") {
                entity.id `should be equal to` -1
            }

            it("should set the active flag of the entity to false") {
                entity.active `should be equal to` false
            }

            it("should remove the entity from the manager") {
                (entity in manager) `should be equal to` false
            }

            it("should add the entity's id to the recycle ids of the entity pool") {
                manager.entityPool.recycledIds.size `should be equal to` 1
                manager.entityPool.recycledIds.contains(entityId)
            }

            it("should add the entity to the free objects of the entity pool") {
                manager.entityPool.free `should be equal to` 1
            }
        }

        describe("creating a new entity that exceeds the current entity size (=2) of the manager") {
            val entities = Array<KECSEntity>()
            beforeEachTest {
                entities.clear()
                repeat(3) {
                    entities.add(manager.obtain())
                }
            }

            it("should increase the maximum amount of possible entities by 75% (=3)") {
                manager.entities.size `should be equal to` 3
            }

            it("should contain all three entities") {
                entities.forEach { (it in manager) `should be equal to` true }
            }
        }
    }
})
