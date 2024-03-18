package com.lokate.kmmsdk.utils.collection

import io.ktor.util.collections.ConcurrentSet

@Suppress("TooManyFunctions")
class ConcurrentSetWithSpecialEquals<Key : Any>(
    val equals: (Key, Key) -> Boolean,
) : MutableSet<Key> {
    private val delegate = ConcurrentSet<Key>()

    private fun removeIf(predicate: (Key) -> Boolean) {
        delegate.find { predicate(it) }?.let {
            delegate.remove(it)
        }
    }

    fun addOrUpdate(key: Key): Boolean {
        removeIf { equals(it, key) }
        return delegate.add(key)
    }

    override fun addAll(elements: Collection<Key>): Boolean {
        var result = true
        elements.forEach { result = add(it) }
        return result
    }

    override val size: Int
        get() = delegate.size

    override fun add(element: Key): Boolean {
        if (contains(element)) {
            return false
        }
        return delegate.add(element)
    }

    override fun clear() {
        delegate.clear()
    }

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun containsAll(elements: Collection<Key>): Boolean =
        elements.all { contains(it) }

    override fun contains(element: Key): Boolean {
        return delegate.any { equals(it, element) }
    }

    override fun iterator(): MutableIterator<Key> = delegate.iterator()

    override fun retainAll(elements: Collection<Key>): Boolean =
        delegate.retainAll(elements.toSet())

    override fun removeAll(elements: Collection<Key>): Boolean =
        delegate.removeAll(elements.toSet())

    override fun remove(element: Key): Boolean = delegate.remove(element)
}
