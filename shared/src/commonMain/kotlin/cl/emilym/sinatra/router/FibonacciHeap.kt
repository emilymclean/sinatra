// Based on https://keithschwarz.com/interesting/code/?dir=fibonacci-heap
package cl.emilym.sinatra.router

class FibonacciHeap<T> {

    companion object {
        private fun <T> merge(one: FibonacciHeap<T>, two: FibonacciHeap<T>): FibonacciHeap<T> {
            val out = FibonacciHeap<T>()
            out.min = mergeLists(one.min, two.min)
            out._size = one.size + two.size

            one._size = 0
            two._size = 0
            one.min = null
            two.min = null

            return out
        }

        private fun <T> mergeLists(one: Entry<T>?, two: Entry<T>?): Entry<T>? {
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

    private class Entry<T>(
        val element: T,
        val priority: Double
    ) {
        var next: Entry<T> = this
        var previous: Entry<T> = this
        var parent: Entry<T>? = null
        var child: Entry<T>? = null
        var degree: Int = 0
        var marked: Boolean = false
    }

    private var min: Entry<T>? = null
    private var _size: Int = 0
    val size: Int get() = _size

    val first: T? get() = min?.element

    fun clear() {
        min = null
    }

    fun add(element: T, priority: Double): Boolean {
        val n = Entry(element, priority)
        min = mergeLists(min, n)
        _size += 1

        return true
    }

    fun pop(): T? {
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

        val treeTable = mutableListOf<Entry<T>?>()
        val toVisit = mutableListOf<Entry<T>>()

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

            if (cursor.priority <= (min?.priority ?: Double.MAX_VALUE)) min = cursor;
        }

        return first.element
    }

    fun isEmpty() = min == null

}