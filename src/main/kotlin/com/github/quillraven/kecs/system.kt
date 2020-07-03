package com.github.quillraven.kecs

interface System {
    var active: Boolean

    fun update(world: World, deltaTime: Float)
}

abstract class IteratingSystem(
    private val family: Family,
    override var active: Boolean = true
) : System {
    override fun update(world: World, deltaTime: Float) = family.iterate { updateEntity(world, it, deltaTime) }

    abstract fun updateEntity(world: World, entityID: Int, deltaTime: Float)
}
