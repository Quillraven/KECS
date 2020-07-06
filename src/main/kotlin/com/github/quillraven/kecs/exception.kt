package com.github.quillraven.kecs

import com.badlogic.gdx.utils.GdxRuntimeException

class MissingComponentException(entityID: Int, componentType: Class<*>) :
    GdxRuntimeException("Entity $entityID is missing a component of type $componentType")