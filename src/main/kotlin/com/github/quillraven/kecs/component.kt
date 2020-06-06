package com.github.quillraven.kecs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IdentityMap
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.ReflectionPool
import java.util.*
import kotlin.math.max
import kotlin.reflect.KClass

/**
 * Common interface of any component of the entity component system.
 * Any component class must have a constructor with no arguments to be
 * able to be [poolable][Pool.Poolable].
 */
interface KECSComponent : Pool.Poolable

data class KECSComponentMapper(val id: Int, val type: KClass<out KECSComponent>)

class KECSComponentManager(
    initialEntityCapacity: Int,
    initialComponentCapacity: Int
) : KECSEntityListener {
    val componentPools = IdentityMap<KClass<out KECSComponent>, ReflectionPool<out KECSComponent>>()
    val entityComponents = Array<Array<KECSComponent>>(false, initialEntityCapacity).apply {
        // fill array with null values to correctly set the size and to be able to call "set(index,value)"
        repeat(initialEntityCapacity) {
            val components = Array<KECSComponent>(false, initialComponentCapacity)
            this.add(components)
            repeat(initialComponentCapacity) {
                components.add(null)
            }
        }
    }
    val entityComponentBits = Array<BitSet>(false, initialEntityCapacity).apply {
        // fill array with null values to correctly set the size and to be able to call "set(index,value)"
        repeat(initialEntityCapacity) {
            this.add(null)
        }
    }
    val componentMappers = IdentityMap<KClass<out KECSComponent>, KECSComponentMapper>()

    inline fun <reified T : KECSComponent> obtain(): T = obtain(T::class) as T

    fun <T : KECSComponent> obtain(type: KClass<T>): KECSComponent {
        if (!componentPools.containsKey(type)) {
            componentPools.put(type, ReflectionPool(type.java))
        }
        return componentPools[type].obtain()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : KECSComponent> free(component: T) {
        (componentPools[component::class] as ReflectionPool<T>).free(component)
    }

    private fun mapper(component: KECSComponent) = mapper(component::class)

    inline fun <reified T : KECSComponent> mapper() = mapper(T::class)

    fun mapper(type: KClass<out KECSComponent>): KECSComponentMapper {
        if (!componentMappers.containsKey(type)) {
            componentMappers.put(type, KECSComponentMapper(componentMappers.size, type))
        }
        return componentMappers[type]
    }

    // slow version
    fun add(entity: KECSEntity, component: KECSComponent) = add(entity, mapper(component), component)

    // fast version
    private fun add(entity: KECSEntity, mapper: KECSComponentMapper, component: KECSComponent) {
        val components = entityComponents[entity.id]
        if (mapper.id >= components.size) {
            // component array is not big enough to store the new component
            // -> resize by 75% and fill it up again with null values
            repeat(max(1, (components.size * 0.75f).toInt())) {
                components.add(null)
                entityComponentBits.add(null)
            }
        }

        if (components.get(mapper.id) == null) {
            components.set(mapper.id, component)
            if (entityComponentBits[entity.id] == null) {
                entityComponentBits[entity.id] = BitSet()
            }
            entityComponentBits[entity.id].set(mapper.id)
        } else {
            throw KECSComponentAlreadyExistingException(entity, mapper.type)
        }
    }

    // slow version
    fun remove(entity: KECSEntity, component: KECSComponent) = remove(entity, mapper(component), component)

    // fast version
    private fun remove(entity: KECSEntity, mapper: KECSComponentMapper, component: KECSComponent) {
        entityComponents[entity.id].set(mapper.id, null)
        free(component)
        entityComponentBits[entity.id].clear(mapper.id)
    }

    // slow version
    inline fun <reified T : KECSComponent> get(entity: KECSEntity) = get(entity, mapper(T::class)) as T

    // fast version
    inline fun <reified T : KECSComponent> get(entity: KECSEntity, mapper: KECSComponentMapper): T {
        val component = entityComponents[entity.id][mapper.id]
        if (component != null) {
            return entityComponents[entity.id][mapper.id] as T
        } else {
            throw KECSMissingComponentException(entity, mapper.type)
        }
    }

    override fun entityAdded(entity: KECSEntity) {
        if (entityComponentBits[entity.id] == null) {
            entityComponentBits[entity.id] = BitSet()
        }
    }

    override fun entityRemoved(entity: KECSEntity) {
        entityComponents[entity.id].forEachIndexed { index, component ->
            if (component != null) {
                free(component)
            }
            entityComponents[entity.id].set(index, null)
        }
        entityComponentBits[entity.id].clear()
    }

    override fun entityResize(newSize: Int) {
        repeat(newSize - entityComponents.size) {
            val size = entityComponents[0].size
            val components = Array<KECSComponent>(false, size)
            entityComponents.add(components)
            repeat(size) {
                components.add(null)
            }
            entityComponentBits.add(null)
        }
    }
}
