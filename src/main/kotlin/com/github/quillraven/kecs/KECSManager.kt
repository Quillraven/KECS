package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import java.util.*
import kotlin.reflect.KClass

class KECSManager(
    initialEntityCapacity: Int = 100,
    initialComponentCapacity: Int = 20
) {
    private val entityManager = KECSEntityManager(this, initialEntityCapacity)
    private val componentManager = KECSComponentManager(initialEntityCapacity, initialComponentCapacity).apply {
        entityManager.addListener(this)
    }
    private val familyManager = KECSFamilyManager()

    private val allOfDSL = Array<KECSComponentMapper>()
    private val noneOfDSL = Array<KECSComponentMapper>()
    private val anyOfDSL = Array<KECSComponentMapper>()
    private val familyDSL = KECSFamilyDSL(allOfDSL, noneOfDSL, anyOfDSL, componentManager)

    fun entity(init: KECSEntity.() -> Unit = {}) = entityManager.obtain().apply { init() }

    fun free(entity: KECSEntity) = entityManager.free(entity)

    operator fun contains(entity: KECSEntity) = entity in entityManager

    inline fun <reified T : KECSComponent> componentFor(entity: KECSEntity): T = componentFor(entity, T::class) as T

    fun <T : KECSComponent> componentFor(entity: KECSEntity, type: KClass<T>) =
        componentManager.obtain(type).apply { componentManager.add(entity, this) }

    fun componentsOf(entity: KECSEntity): Array<KECSComponent> = componentManager.entityComponents[entity.id]

    fun componentBitsOf(entity: KECSEntity): BitSet = componentManager.entityComponentBits[entity.id]

    fun mapper(type: KClass<out KECSComponent>) = componentManager.mapper(type)

    fun family(init: KECSFamilyDSL.() -> Unit): KECSFamily {
        familyDSL.run {
            init()
            return familyManager.family(allOfDSL, noneOfDSL, anyOfDSL, entityManager.entities.size)
        }
    }
}
