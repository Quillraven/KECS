package com.github.quillraven.kecs

import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.reflect.KClass

/**
 * Common type of exception thrown by the entity component system API.
 */
open class KECSException(message: String) : GdxRuntimeException(message)

/**
 * [KECSException] that is thrown when adding a [KECSComponent] to a [KECSEntity] that
 * already has this type of component.
 */
class KECSComponentAlreadyExistingException(entity: KECSEntity, type: KClass<out KECSComponent>) :
    KECSException("Component of type $type already exists for entity $entity")

/**
 * [KECSException] that is thrown when removing a non-existing [KECSComponent] from a [KECSEntity].
 */
class KECSMissingComponentException(entity: KECSEntity, type: KClass<out KECSComponent>) :
    KECSException("Component of type $type does not exist for entity $entity")
