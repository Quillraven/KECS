package com.github.quillraven.kecs

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Pool
import kotlin.reflect.KClass

data class KECSEntity(
    val manager: KECSManager,
    private val componentsInternal: ObjectMap<KClass<out KECSComponent>, KECSComponent> = ObjectMap()
) : Pool.Poolable {
    val components = ComponentMap(componentsInternal)
    val componentSize: Int get() = components.size

    /**
     * Adds a [KECSComponent] to the entity and initializes the component according to the [init] block.
     * Throws an [KECSComponentAlreadyExistingException] if the component already exists.
     * The component is retrieved from a [Pool] out of the [KECSManager].
     */
    inline fun <reified T : KECSComponent> add(noinline init: T.() -> (Unit) = {}): T = add(T::class, init)

    /**
     * Adds a [KECSComponent] to the entity and initializes the component according to the [init] block.
     * Throws an [KECSComponentAlreadyExistingException] if the component already exists.
     * The component is retrieved from a [Pool] out of the [KECSManager].
     */
    fun <T : KECSComponent> add(type: KClass<T>, init: T.() -> (Unit) = {}): T {
        if (type in components) {
            throw KECSComponentAlreadyExistingException(this, type)
        }
        val component = manager.component(type).apply(init)
        componentsInternal.put(type, component)
        return component
    }

    /**
     * Returns an existing [KECSComponent] of the entity or throws a [KECSMissingComponentException]
     * if the component does not exist.
     */
    inline fun <reified T : KECSComponent> get(): T {
        val type = T::class
        val component = components[type] ?: throw KECSMissingComponentException(this, type)
        return component as T
    }

    /**
     * Removes an existing [KECSComponent] of the entity or throws a [KECSMissingComponentException]
     * if the component does not exist.
     * Returns the component back to a [Pool] of the [KECSManager].
     */
    inline fun <reified T : KECSComponent> remove() = remove(T::class)

    /**
     * Removes an existing [KECSComponent] of the entity or throws a [KECSMissingComponentException]
     * if the component does not exist.
     * Returns the component back to a [Pool] of the [KECSManager].
     */
    fun <T : KECSComponent> remove(type: KClass<T>) {
        val component = components[type] ?: throw KECSMissingComponentException(this, type)
        manager.freeComponent(component)
        componentsInternal.remove(type)
    }

    /**
     * Removes this entity back to the [Pool] of the [KECSManager].
     * This will also call [Pool.free] for all [components][KECSComponent] of the entity,
     * returning them back to the related component [Pool] of the [KECSManager].
     */
    fun free() = manager.freeEntity(this)

    /**
     * Calls [Pool.free] for all for all [components][KECSComponent] of the entity,
     * returning them back to the related component [Pool] of the [KECSManager].
     */
    override fun reset() {
        componentsInternal.values().forEach { manager.freeComponent(it) }
        componentsInternal.clear()
    }
}
