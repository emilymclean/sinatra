// Based on https://keithschwarz.com/interesting/code/?dir=fibonacci-heap
package cl.emilym.sinatra.router

interface Heap<T,P: Comparable<P>> {
    val size: Int
    val first: T?

    fun clear()
    fun add(element: T, priority: P): Boolean
    fun pop(): T?
    fun isEmpty(): Boolean
}

class NaiveHeap<T, P: Comparable<P>>: Heap<T,P> {

    private data class Entry<T, P: Comparable<P>>(
        val element: T,
        val priority: P
    )

    private val data = mutableListOf<Entry<T,P>>()

    override val size: Int
        get() = data.size
    override val first: T?
        get() = data.minByOrNull { it.priority }?.element

    override fun clear() = data.clear()

    override fun add(element: T, priority: P): Boolean {
        return data.add(Entry(element, priority))
    }

    override fun pop(): T? {
        return data.minByOrNull { it.priority }?.also {
            data.remove(it)
        }?.element
    }

    override fun isEmpty() = data.isEmpty()
}

class FibonacciHeap<T,P: Comparable<P>>: Heap<T,P> {

    companion object {
        private fun <T,P: Comparable<P>> merge(one: FibonacciHeap<T,P>, two: FibonacciHeap<T,P>): FibonacciHeap<T,P> {
            val out = FibonacciHeap<T,P>()
            out.min = mergeLists(one.min, two.min)
            out._size = one.size + two.size

            one._size = 0
            two._size = 0
            one.min = null
            two.min = null

            return out
        }

        private fun <T,P: Comparable<P>> mergeLists(one: Entry<T,P>?, two: Entry<T,P>?): Entry<T,P>? {
            when {
                one == null && two == null -> return null
                one == null -> return two
                two == null -> return one
            }

            val oneNext = one!!.next
            one.next = two!!.next
            one.next.previous = one
            two.next = oneNext
            two.next.previous = two

            return when(one.priority < two.priority) {
                true -> one
                false -> two
            }
        }
    }

    private class Entry<T,P: Comparable<P>>(
        val element: T,
        val priority: P
    ) {
        var next: Entry<T,P> = this
        var previous: Entry<T,P> = this
        var parent: Entry<T,P>? = null
        var child: Entry<T,P>? = null
        var degree: Int = 0
        var marked: Boolean = false
    }

    private var min: Entry<T,P>? = null
    private var _size: Int = 0
    override val size: Int get() = _size

    override val first: T? get() = min?.element

    override fun clear() {
        min = null
    }

    override fun add(element: T, priority: P): Boolean {
        val n = Entry(element, priority)
        min = mergeLists(min, n)
        _size += 1

        return true
    }

    override fun pop(): T? {
        if (isEmpty()) return null
        _size -= 1

        val first = min!!

        when {
            first.next == first -> min = null
            else -> {
                first.previous.next = first.next
                first.next.previous = first.previous
                min = first.next
            }
        }

        if (first.child != null) {
            var current = first.child
            do {
                current?.parent = null
                current = current?.next
            } while(current != first.child)
        }

        min = mergeLists(min, first.child) ?: return first.element

        val treeTable = mutableListOf<Entry<T,P>?>()
        val toVisit = mutableListOf<Entry<T,P>>()

        // Thanks kotlin for not adding a regular fucking for loop
        var current = min
        while ((toVisit.isEmpty() || toVisit[0] != current) && current != null) {
            toVisit.add(current)
            current = current.next
        }

        for (current in toVisit) {
            var cursor = current
            while (true) {
                while (cursor.degree >= treeTable.size) {
                    treeTable.add(null)
                }

                if (treeTable[cursor.degree] == null) {
                    treeTable[cursor.degree] = cursor
                    break
                }

                val other = treeTable[cursor.degree]!!
                treeTable[cursor.degree] = null

                val min = if (other.priority < cursor.priority) other else cursor
                val max = if (other.priority < cursor.priority) cursor else other

                max.next.previous = max.previous
                max.previous.next = max.next

                max.next = max
                max.previous = max
                min.child = mergeLists(min.child, max)

                max.marked = false
                min.degree += 1

                cursor = min
            }

            if (cursor.priority <= min!!.priority) min = cursor;
        }

        return first.element
    }

    override fun isEmpty() = min == null

}