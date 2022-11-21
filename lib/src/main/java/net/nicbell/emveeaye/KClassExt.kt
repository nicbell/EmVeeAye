package net.nicbell.emveeaye

import kotlin.reflect.KClass

fun KClass<*>.logName() = "${this.java.superclass.simpleName}.${this.simpleName}"