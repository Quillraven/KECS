package com.github.quillraven.kecs

class KECSManager(
    initialEntityCapacity: Int = 100,
    initialComponentCapacity: Int = 20
) {
    val entityManager = KECSEntityManager(this, initialEntityCapacity)
    val componentManager = KECSComponentManager(initialEntityCapacity, initialComponentCapacity)

    fun entity(init: KECSEntity.() -> Unit = {}) = entityManager.obtain().apply { init() }
}
