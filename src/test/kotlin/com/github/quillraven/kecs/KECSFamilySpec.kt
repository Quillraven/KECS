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
        val manager by memoized { KECSFamilyManager() }
        val componentManager by memoized { KECSComponentManager(1, 3) }

        describe("creating a new family in an empty manager") {
            val all = Array<KECSComponentMapper>()
            val none = Array<KECSComponentMapper>()
            val any = Array<KECSComponentMapper>()
            val initialEntityCapacity = 2
            lateinit var family: KECSFamily
            beforeEachTest {
                all.add(componentManager.mapper<TransformComponent>())
                none.add(componentManager.mapper<TransformComponent>())
                any.add(componentManager.mapper<TransformComponent>())
                family = manager.family(all, none, any, initialEntityCapacity)
            }

            it("should create a new family") {
                (family in manager) `should be equal to` true
            }

            it("should create a new entity array with the initial entity capacity") {
                manager.familyEntities[family].items.size `should be equal to` initialEntityCapacity
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
                existingFamily = manager.family(Array(), Array(), Array(), 2)
                newFamily = manager.family(Array(), Array(), Array(), 2)
            }

            it("should return the existing family") {
                existingFamily `should be` newFamily
            }

            it("should not add a duplicated family to the manager") {
                manager.familyEntities.size `should be equal to` 1
            }
        }

        describe("matching a component configuration against a family") {
            val all = Array<KECSComponentMapper>()
            val none = Array<KECSComponentMapper>()
            val any = Array<KECSComponentMapper>()
            val initialEntityCapacity = 2
            lateinit var everythingFamily: KECSFamily
            lateinit var allFamily: KECSFamily
            lateinit var anyFamily: KECSFamily
            beforeEachTest {
                val transformMapper = componentManager.mapper<TransformComponent>()
                val physiqueMapper = componentManager.mapper<PhysicComponent>()
                val noneMapper = componentManager.mapper<RemoveComponent>()
                val playerMapper = componentManager.mapper<PlayerComponent>()
                all.add(transformMapper, physiqueMapper)
                none.add(noneMapper)
                any.add(playerMapper)
                everythingFamily = manager.family(all, none, any, initialEntityCapacity)
                allFamily = manager.family(all = all, initialEntityCapacity = initialEntityCapacity)
                anyFamily = manager.family(any = any, initialEntityCapacity = initialEntityCapacity)
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
        }
    }
})
