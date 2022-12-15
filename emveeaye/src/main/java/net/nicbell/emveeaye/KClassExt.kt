package net.nicbell.emveeaye

import kotlin.reflect.KClass

/**
 * Turns sealed class names into a useful format for logs - ParentName.ChildName
 */
fun KClass<*>.logName() = "${this.java.superclass.simpleName}.${this.simpleName}"