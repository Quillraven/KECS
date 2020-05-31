package com.github.quillraven.kecs

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Pool
import kotlin.reflect.KClass

data class KECSEntity(val manager: KECSManager) : Pool.Poolable {
    private val componentsInternal = ObjectMap<KClass<out KECSComponent>, KECSComponent>()
    val components = ComponentMap(componentsInternal)
    val componentSize: Int get() = components.size

    inline fun <reified T : KECSComponent> add(noinline init: T.() -> (Unit) = {}): T = add(T::class, init)

    fun <T : KECSComponent> add(type: KClass<T>, init: T.() -> (Unit) = {}): T {
        if (type in components) {
            throw KECSComponentAlreadyExistingException(this, type)
        }
        val component = manager.component(type).apply(init)
        componentsInternal.put(type, component)
        return component
    }

    inline fun <reified T : KECSComponent> get(): T {
        val type = T::class
        val component = components[type] ?: throw KECSMissingComponentException(this, type)
        return component as T
    }

    inline fun <reified T : KECSComponent> remove() = remove(T::class)

    fun <T : KECSComponent> remove(type: KClass<T>) {
        val component = components[type] ?: throw KECSMissingComponentException(this, type)
        manager.freeComponent(component)
        componentsInternal.remove(type)
    }

    fun free() = manager.freeEntity(this)

    override fun reset() {
        componentsInternal.values().forEach { manager.freeComponent(it) }
        componentsInternal.clear()
    }
}
