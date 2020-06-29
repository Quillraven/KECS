package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.github.quillraven.kecs.component.PhysicComponent
import com.github.quillraven.kecs.component.PlayerComponent
import com.github.quillraven.kecs.component.RemoveComponent
import com.github.quillraven.kecs.component.TransformComponent
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

@Suppress("UNUSED")
object KECSFamilySpec : Spek({
    describe("A Family Manager") {
        val manager by memoized { KECSManager(1, 3) }
        val componentManager by memoized { KECSComponentManager(1, 3) }
        val familyManager by memoized { KECSFamilyManager(componentManager) }
        val entityManager by memoized { KECSEntityManager(componentManager, 1) }

        describe("creating a new family in an empty manager") {
            val all = Array<KECSComponentMapper<out KECSComponent>>()
            val none = Array<KECSComponentMapper<out KECSComponent>>()
            val any = Array<KECSComponentMapper<out KECSComponent>>()
            val initialEntityCapacity = 2
            lateinit var family: KECSFamily
            beforeEachTest {
                all.add(componentManager.mapper<TransformComponent>())
                none.add(componentManager.mapper<TransformComponent>())
                any.add(componentManager.mapper<TransformComponent>())
                family = familyManager.family(all, none, any, initialEntityCapacity)
            }

            it("should create a new family") {
                (family in familyManager) `should be equal to` true
            }

            it("should create a new empty entity collection") {
                familyManager.familyEntities[family].size `should be equal to` 0
            }

            it("should reference the empty entity collection") {
                family.entities `should be equal to` familyManager.familyEntities[family]
            }

            it("should set the all BitSet of the family") {
                all.forEach { family.allSet[it.id] `should be equal to` true }
            }

            it("should set the none BitSet of the family") {
                none.forEach { family.noneSet[it.id] `should be equal to` true }
            }

            it("should set the any BitSet of the family") {
                any.forEach { family.anySet[it.id] `should be equal to` true }
            }
        }

        describe("creating a family that already exists") {
            lateinit var existingFamily: KECSFamily
            lateinit var newFamily: KECSFamily
            beforeEachTest {
                existingFamily = familyManager.family(Array(), Array(), Array(), 2)
                newFamily = familyManager.family(Array(), Array(), Array(), 2)
            }

            it("should return the existing family") {
                existingFamily `should be` newFamily
            }

            it("should not add a duplicated family to the manager") {
                familyManager.familyEntities.size `should be equal to` 1
            }
        }

        describe("matching a component configuration against a family") {
            val all = Array<KECSComponentMapper<out KECSComponent>>()
            val none = Array<KECSComponentMapper<out KECSComponent>>()
            val any = Array<KECSComponentMapper<out KECSComponent>>()
            val initialEntityCapacity = 2
            lateinit var everythingFamily: KECSFamily
            lateinit var allFamily: KECSFamily
            lateinit var anyFamily: KECSFamily
            lateinit var emptyFamily: KECSFamily
            beforeEachTest {
                val transformMapper = componentManager.mapper<TransformComponent>()
                val physiqueMapper = componentManager.mapper<PhysicComponent>()
                val noneMapper = componentManager.mapper<RemoveComponent>()
                val playerMapper = componentManager.mapper<PlayerComponent>()
                all.add(transformMapper, physiqueMapper)
                none.add(noneMapper)
                any.add(playerMapper)
                everythingFamily = familyManager.family(all, none, any, initialEntityCapacity)
                allFamily = familyManager.family(all = all, initialEntityCapacity = initialEntityCapacity)
                anyFamily = familyManager.family(any = any, initialEntityCapacity = initialEntityCapacity)
                emptyFamily = familyManager.family(initialEntityCapacity = initialEntityCapacity)
            }

            it("should return true if components do have all components of family's allSet and any components of the family's anySet") {
                val components = BitSet()
                all.forEach { components.set(it.id) }
                components.set(any.first().id)
                (components in everythingFamily) `should be equal to` true
            }

            it("should return false if components do not have all components of family's allSet") {
                val components = BitSet()
                all.forEach { components.set(it.id) }
                any.forEach { components.set(it.id) }
                components.clear(all.first().id)
                (components in everythingFamily) `should be equal to` false
            }

            it("should return false if components do not have any component of family's anySet") {
                val components = BitSet()
                all.forEach { components.set(it.id) }
                (components in everythingFamily) `should be equal to` false
            }

            it("should return false if components have any component of family's noneSet") {
                val components = BitSet()
                all.forEach { components.set(it.id) }
                components.set(any.first().id)
                components.set(none.first().id)
                (components in everythingFamily) `should be equal to` false
            }

            it("should return true if components do have all components of family's allSet") {
                val components = BitSet()
                all.forEach { components.set(it.id) }
                (components in allFamily) `should be equal to` true
            }

            it("should return true if components do have any components of family's anySet") {
                val components = BitSet()
                components.set(any.first().id)
                (components in anyFamily) `should be equal to` true
            }

            it("should return always true for empty families") {
                val components = BitSet()
                (components in emptyFamily) `should be equal to` true
            }
        }

        describe("matching an entity against a family") {
            lateinit var entityAll: KECSEntity
            lateinit var entityNone: KECSEntity
            lateinit var family: KECSFamily
            beforeEachTest {
                val all = Array<KECSComponentMapper<out KECSComponent>>()
                val transformMapper = componentManager.mapper<TransformComponent>()
                val physiqueMapper = componentManager.mapper<PhysicComponent>()
                all.add(transformMapper, physiqueMapper)
                family = familyManager.family(all, initialEntityCapacity = 1)
                entityAll = manager.entity {
                    with<PhysicComponent>()
                    with<TransformComponent>()
                }
                entityNone = manager.entity()
            }

            it("should return true if the entity's components matches the family") {
                (entityAll in family) `should be equal to` true
            }

            it("should return false if the entity's components do not match the family") {
                (entityNone in family) `should be equal to` false
            }
        }

        describe("creating an entity with a non-empty family manager") {
            lateinit var entityAll: KECSEntity
            lateinit var entityNone: KECSEntity
            lateinit var family: KECSFamily
            beforeEachTest {
                entityManager.addListener(componentManager)
                entityManager.addListener(familyManager)

                val all = Array<KECSComponentMapper<out KECSComponent>>()
                val transformMapper = componentManager.mapper<TransformComponent>()
                val physiqueMapper = componentManager.mapper<PhysicComponent>()
                all.add(transformMapper, physiqueMapper)
                family = familyManager.family(all, initialEntityCapacity = 1)
                val components = Array<KECSComponent>().apply {
                    add(componentManager.obtain<PhysicComponent>())
                    add(componentManager.obtain<TransformComponent>())
                }
                entityAll = entityManager.obtain(components)
                entityNone = entityManager.obtain()
            }

            it("should add the entity to the entities collection that matches its components") {
                (entityAll in familyManager.familyEntities[family]) `should be equal to` true
            }

            it("should not add the entity to any entities collection if its components do not match any family") {
                familyManager.familyEntities.values().forEach {
                    (entityNone in it) `should be equal to` false
                }
            }
        }

        describe("updating an entity by adding components with a non-empty family manager") {
            lateinit var entity: KECSEntity
            lateinit var family: KECSFamily
            beforeEachTest {
                entityManager.addListener(componentManager)
                entityManager.addListener(familyManager)

                val all = Array<KECSComponentMapper<out KECSComponent>>()
                val transformMapper = componentManager.mapper<TransformComponent>()
                val physiqueMapper = componentManager.mapper<PhysicComponent>()
                all.add(transformMapper, physiqueMapper)
                family = familyManager.family(all, initialEntityCapacity = 1)
                entity = entityManager.obtain()
            }

            it("should add the entity to the entities collection that matches its components") {
                (entity in familyManager.familyEntities[family]) `should be equal to` false
                entity.update {
                    add<PhysicComponent>()
                    add<TransformComponent>()
                }
                (entity in familyManager.familyEntities[family]) `should be equal to` true
            }
        }

        describe("updating an entity by removing components with a non-empty family manager") {
            lateinit var entity: KECSEntity
            lateinit var family: KECSFamily
            beforeEachTest {
                entityManager.addListener(componentManager)
                entityManager.addListener(familyManager)

                val all = Array<KECSComponentMapper<out KECSComponent>>()
                val transformMapper = componentManager.mapper<TransformComponent>()
                val physiqueMapper = componentManager.mapper<PhysicComponent>()
                all.add(transformMapper, physiqueMapper)
                family = familyManager.family(all, initialEntityCapacity = 1)
                val components = Array<KECSComponent>().apply {
                    add(componentManager.obtain<PhysicComponent>())
                    add(componentManager.obtain<TransformComponent>())
                }
                entity = entityManager.obtain(components)
            }

            it("should remove the entity from the entities collection that was matching it before") {
                (entity in familyManager.familyEntities[family]) `should be equal to` true
                entity.update {
                    remove<TransformComponent>()
                }
                (entity in familyManager.familyEntities[family]) `should be equal to` false
            }
        }
    }
})
