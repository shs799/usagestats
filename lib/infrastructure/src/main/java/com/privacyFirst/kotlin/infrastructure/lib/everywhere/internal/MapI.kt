package com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal

internal interface MapI<K,V>:MutableMap<K,V> {
    override val entries: EntrySetI<K,V>
    override val keys: KeySetI<K>
    override val values: MapCollectionI<V>
}