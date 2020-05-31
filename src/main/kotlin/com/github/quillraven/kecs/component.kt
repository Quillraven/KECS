package com.github.quillraven.kecs

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.ReflectionPool
import kotlin.reflect.KClass

/**
 * Common interface of any component of the entity component system.
 * Any component class must have a constructor with no arguments to be
 * able to be [poolable][Pool.Poolable].
 */
interface KECSComponent : Pool.Poolable

/**
 * Immutable map of components.
 * Every [KECSEntity] has a [ComponentMap] to get read access to any [KECSComponent] that it has.
 */
class ComponentMap(private val components: ObjectMap<KClass<out KECSComponent>, KECSComponent>) {
    val size: Int get() = components.size

    /**
     * Returns true if and only if the given [key] is part of the component map.
     */
    operator fun contains(key: KClass<out KECSComponent>) = components.containsKey(key)

    /**
     * Returns the [KECSComponent] stored with the given [key] or null if this type of component is missing.
     */
    operator fun get(key: KClass<out KECSComponent>): KECSComponent? = components[key]
}

/**
 * Utility class to handle multiples [pools][ReflectionPool] for different types of [KECSComponent].
 * Every [KECSManager] has a [ComponentPools] instance to create pools and to free components that
 * are no longer needed by a [KECSEntity].
 */
class ComponentPools {
    private val pools = ObjectMap<KClass<out KECSComponent>, ReflectionPool<out KECSComponent>>()

    /**
     * Returns a [ReflectionPool] of the given [type]. The pool will be created if it does not exist
     * and will be stored for later use.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : KECSComponent> get(type: KClass<T>): ReflectionPool<T> {
        if (!pools.containsKey(type)) {
            pools.put(type, ReflectionPool(type.java))
        }
        return pools[type] as ReflectionPool<T>
    }

    /**
     * Returns the given [component] back to its related [ReflectionPool] which makes it available
     * for the next call to [Pool.obtain].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : KECSComponent> free(component: T) {
        (pools[component::class] as ReflectionPool<T>).free(component)
    }
}
