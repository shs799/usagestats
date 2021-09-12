package com.privacyFirst.usageStats.underconstruction

class NewMapType {
    private object A {
        abstract class MapA<K, V> : I.MapI<K, V> {
            /*override val entries: I.EntriesI<K, V>
                get() {
                    val e=mEntries
                    return if (e==null){
                        val e=EntriesA(this)
                        mEntries=e
                        e
                    }else{
                        e
                    }
                }*/
            abstract var mEntries: I.EntriesI<K, V>

            /*override val values: I.ValuesI<V>
                get() {
                    val v=mValues
                    return if (v==null){
                        val v=ValuesA(this)
                        mValues=v
                        v
                    }else{
                        v
                    }
                }*/
            abstract var mValues: I.ValuesI<V>

            /*override val keys: I.KeysI<K>
                get() {
                    val k=mKeys
                    return if (k==null){
                        val k=KeysA(this)
                        mKeys=k
                        k
                    }else{
                        k
                    }
                }*/
            abstract var mKeys: I.KeysI<K>


        }

        abstract class EntriesA<K, V>(private val map: I.MapI<K, V>) : I.EntriesI<K, V> {


            override val size: Int
                get() = map.size

            override fun clear() = map.clear()

        }

        abstract class ValueList<K, V>(private val map: MapA<K, V>) : I.ValuesI<V> {
            override val size: Int
                get() = TODO("Not yet implemented")

            override fun clear() {
                TODO("Not yet implemented")
            }


        }

        object I {
            interface MapI<K, V> : MutableMap<K, V> {

                override val entries: EntriesI<K, V>
                override val values: ValuesI<V>
                override val keys: KeysI<K>

            }

            interface KeysI<V> : KeyI<V> {

                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun addAll(elements: Collection<V>) =
                    throw UnsupportedOperationException()


                override fun removeAll(elements: Collection<V>): Boolean {
                    TODO("Not yet implemented")
                }

                override fun retainAll(elements: Collection<V>): Boolean {
                    TODO("Not yet implemented")
                }

                override fun contains(element: V): Boolean {
                    TODO("Not yet implemented")
                }

                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun add(element: V) = throw UnsupportedOperationException()


                override fun remove(element: V): Boolean {
                    TODO("Not yet implemented")
                }

                override fun isEmpty(): Boolean = size==0
            }

            interface ValuesI<V> : CollectionAllOperationI<V> {

                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun addAll(elements: Collection<V>)=
                    throw UnsupportedOperationException()


                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun removeAll(elements: Collection<V>)=
                    throw UnsupportedOperationException()


                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun retainAll(elements: Collection<V>)=
                    throw UnsupportedOperationException()


                override fun contains(element: V): Boolean {
                    this.forEach { E -> if (E === element) return true }
                    return false
                }

                override fun containsAll(elements: Collection<V>): Boolean {
                    return super.containsAll(elements)
                }

                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun add(element: V)=
                    throw UnsupportedOperationException()


                @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
                override fun remove(element: V)=
                    throw UnsupportedOperationException()

                override fun isEmpty(): Boolean = size==0

            }

            interface EntriesI<K, V> : EntryI<K, V> {
                fun add(element: MapEntryI<K, V>): Boolean

                fun addAll(elements: CollectionMapEntryI<K, V>): Boolean {
                    var modify = false
                    if (this === elements) return modify
                    elements.forEach { i -> if (add(i)) modify = true }
                    return modify
                }

                fun contains(element: MapEntryI<K, V>): Boolean

                fun containsAll(elements: CollectionMapEntryI<K, V>): Boolean {
                    if (this === elements) return true
                    elements.forEach { v ->
                        if (!contains(v)) return false
                    }
                    return true
                }

                fun remove(element: MapEntryI<K, V>): Boolean

                fun removeAll(elements: CollectionMapEntryI<K, V>): Boolean {
                    var modify = false
                    if (this === elements) return modify
                    elements.forEach { i -> if (remove(i)) modify = true }
                    return modify
                }

                fun retainAll(elements: CollectionMapEntryI<K, V>): Boolean {
                    TODO("do it")
                }


                override fun isEmpty(): Boolean = size==0

            }

            private interface CollectionAllOperationI<E> : MutableCollection<E> {
                override fun addAll(elements: Collection<E>): Boolean {
                    var modify = false
                    if (this === elements) return modify
                    elements.forEach { i -> if (add(i)) modify = true }
                    return modify
                }

                override fun removeAll(elements: Collection<E>): Boolean {
                    var modify = false
                    if (this === elements) return modify
                    elements.forEach { i -> if (remove(i)) modify = true }
                    return modify
                }

                override fun containsAll(elements: Collection<E>): Boolean {
                    if (this === elements) return true
                    elements.forEach { v ->
                        if (!contains(v)) return false
                    }
                    return true
                }

                override fun retainAll(elements: Collection<E>): Boolean {
                    var modify = false
                    if (this === elements) return modify
                    val i = iterator()
                    while (i.hasNext()) {
                        if (elements.contains(i.next())) {
                            i.remove()
                            modify = true
                        }
                    }
                    return modify
                }
            }

            private interface EntryI<K, V> : CollectionAllOperationI<MutableMap.MutableEntry<K, V>>,
                SetMMapEntryI<K, V> {

                override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
                    super.addAll(elements)

                override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
                    super.removeAll(elements)

                override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
                    super.retainAll(elements)

                override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
                    super.containsAll(elements)

            }

            private interface KeyI<K> : CollectionAllOperationI<K>, MutableSet<K> {
                override fun addAll(elements: Collection<K>) =
                    super.addAll(elements)

                override fun removeAll(elements: Collection<K>) =
                    super.removeAll(elements)

                override fun retainAll(elements: Collection<K>): Boolean =
                    super.retainAll(elements)

                override fun containsAll(elements: Collection<K>) =
                    super.containsAll(elements)

            }

            private interface SetMMapEntryI<K, V> : MutableSet<MutableMap.MutableEntry<K, V>>
            interface CollectionMapEntryI<K, V> : Collection<MapEntryI<K, V>>
            interface MapEntryI<K, V> : Map.Entry<K, V>
        }
    }
}