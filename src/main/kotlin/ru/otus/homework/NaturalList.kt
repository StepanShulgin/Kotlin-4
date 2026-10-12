package ru.otus.homework

/**
 * Список натуральных чисел от 1 до n
 * @param n Последнее натуральное число в списке
 */
class NaturalList(n: Int) : List<Int> {
    init {
        require(n >= 0) { "Size must be non-negative: $n" }
    }

    override val size: Int = n

    override fun get(index: Int): Int = if (index in 0 until size) {
        index + 1
    } else {
        throw NoSuchElementException("No such index $index. Total elements: $size")
    }

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<Int> = NaturalIterator(size)

    override fun listIterator(): ListIterator<Int> = NaturalListIterator(size)

    override fun listIterator(index: Int): ListIterator<Int> = NaturalListIterator(size, index)

    override fun contains(element: Int): Boolean = element in 1..size

    override fun indexOf(element: Int): Int = if (contains(element)) {
        element - 1
    } else {
        -1
    }

    override fun lastIndexOf(element: Int): Int = indexOf(element)

    /**
     * Вернуть под-список этого списка, включая [fromIndex] и НЕ включая [toIndex]
     */
    override fun subList(fromIndex: Int, toIndex: Int): List<Int> {
        if (fromIndex < 0 || toIndex > size) {
            throw IndexOutOfBoundsException("Range: $fromIndex..<$toIndex, size: $size")
        }
        require(fromIndex <= toIndex) { "fromIndex ($fromIndex) exceeds toIndex ($toIndex)" }
        return object : AbstractList<Int>() {
            override val size: Int = toIndex - fromIndex

            override fun get(index: Int): Int {
                if (index !in 0 until size) {
                    throw IndexOutOfBoundsException("Index: $index, size: $size")
                }
                return this@NaturalList[fromIndex + index]
            }
        }
    }

    /**
     * Returns true if list contains all numbers in the collection
     */
    override fun containsAll(elements: Collection<Int>): Boolean = elements.all(::contains)

    override fun toString(): String {
        return "NaturalList(1..$size)"
    }

    /**
     * Функция должна возвращать true, если сравнивается с другой реализацией списка тех же чисел
     * Например, NaturalList(5) должен быть равен listOf(1,2,3,4,5)
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is List<*> || size != other.size) return false
        return other.withIndex().all { (index, value) -> value == index + 1 }
    }

    /**
     * Функция должна возвращать тот же hash-code, что и список другой реализации тех же чисел
     * Например, NaturalList(5).hashCode() должен быть равен listOf(1,2,3,4,5).hashCode()
     */
    override fun hashCode(): Int = fold(1) { hash, element -> 31 * hash + element.hashCode() }
}

private class NaturalIterator(private val n: Int) : Iterator<Int> {
    private var index = 0
    override fun hasNext(): Boolean = index < n
    override fun next(): Int = if (hasNext()) {
        ++index
    } else {
        throw NoSuchElementException()
    }
}

private class NaturalListIterator(private val n: Int, index: Int = 0) : ListIterator<Int> {
    init {
        if (index !in 0..n) {
            throw IndexOutOfBoundsException("Index: $index, size: $n")
        }
    }

    private var index: Int = index
    override fun hasNext(): Boolean = index < n
    override fun hasPrevious(): Boolean = index > 0
    override fun next(): Int = if (hasNext()) {
        ++index
    } else {
        throw NoSuchElementException()
    }
    override fun nextIndex(): Int = index
    override fun previous(): Int = if (hasPrevious()) {
        index--
    } else {
        throw NoSuchElementException()
    }
    override fun previousIndex(): Int = index - 1
}
