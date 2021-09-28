package com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal

internal abstract class NodeA<K, V> : MutableMap.MutableEntry<K, V> {
    override val value: V
        get() = TODO("Not yet implemented")

    override fun hashCode() = key.hashCode() xor value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is Map.Entry<*, *>) {
            if (key == other.key &&
                value == other.value
            ) return true
        }
        return false
    }

    override fun toString() = "$key=$value"
}