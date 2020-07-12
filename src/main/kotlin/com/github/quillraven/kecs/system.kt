package com.github.quillraven.kecs

interface System {
    var active: Boolean

    fun update(deltaTime: Float)
}

abstract class IteratingSystem(
    private val family: Family,
    override var active: Boolean = true
) : System {
    override fun update(deltaTime: Float) = family.iterate { updateEntity(it, deltaTime) }

    abstract fun updateEntity(entityID: Int, deltaTime: Float)
}
