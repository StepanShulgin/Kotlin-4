package ru.otus.homework

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.otus.homework.mapswap.swap
import ru.otus.homework.persons.Person
import ru.otus.homework.persons.sortByAge
import ru.otus.homework.persons.sortByName

class CollectionsEdgeCasesTest {
    @Test
    fun sortsByAgeWithoutOverflowAndPreservesTies() {
        val youngest = Person("Аня", "Иванова", Int.MIN_VALUE)
        val first = Person("Иван", "Петров", 30)
        val second = Person("Пётр", "Петров", 30)
        val oldest = Person("Яна", "Сидорова", Int.MAX_VALUE)
        val source = listOf(first, youngest, oldest, second)

        assertEquals(listOf(oldest, first, second, youngest), source.sortByAge())
        assertEquals(listOf(first, youngest, oldest, second), source)
    }

    @Test
    fun sortsBySurnameThenNameAndPreservesTies() {
        val first = Person("Анна", "Иванова", 25)
        val second = Person("Анна", "Иванова", 30)
        val third = Person("Яна", "Иванова", 20)
        val fourth = Person("Анна", "Петрова", 35)

        assertEquals(listOf(first, second, third, fourth), listOf(third, fourth, first, second).sortByName())
        assertTrue(emptyList<Person>().sortByAge().isEmpty())
        assertTrue(emptyList<Person>().sortByName().isEmpty())
    }

    @Test
    fun swapsEmptyAndNullableMaps() {
        assertEquals(emptyMap<String, Int>(), emptyMap<Int, String>().swap())
        assertEquals(mapOf(null to 1, "nothing" to null), mapOf<Int?, String?>(1 to null, null to "nothing").swap())
    }

    @Test
    fun duplicateMapValuesKeepLastKeyWithoutChangingSource() {
        val source = linkedMapOf(1 to "one", 2 to "one", 3 to "three")
        val swapped = source.swap()

        assertEquals(mapOf("one" to 2, "three" to 3), swapped)
        assertEquals(listOf("one", "three"), swapped.keys.toList())
        assertEquals(linkedMapOf(1 to "one", 2 to "one", 3 to "three"), source)
    }

    @Test
    fun rejectsNegativeSize() {
        assertThrows(IllegalArgumentException::class.java) { NaturalList(-1) }
    }

    @Test
    fun containsAllHandlesEmptyCollectionsAndDuplicates() {
        assertTrue(NaturalList(0).containsAll(emptyList()))
        assertTrue(NaturalList(3).containsAll(listOf(1, 1, 3)))
        assertFalse(NaturalList(0).containsAll(listOf(1)))
        assertFalse(NaturalList(3).containsAll(listOf(0)))
        assertFalse(NaturalList(3).containsAll(listOf(-1)))
        assertFalse(NaturalList(3).containsAll(listOf(4)))
    }

    @Test
    fun equalityAndHashCodeMatchStandardLists() {
        for (size in 0..50) {
            val natural = NaturalList(size)
            val expected = List(size) { it + 1 }

            assertTrue(natural == natural)
            assertEquals(expected, natural)
            assertEquals(natural, expected)
            assertEquals(NaturalList(size), natural)
            assertEquals(expected.hashCode(), natural.hashCode())
        }
    }

    @Test
    fun equalityRejectsDifferentSizesValuesAndTypes() {
        val list = NaturalList(3)

        assertNotEquals(list, null)
        assertNotEquals(list, listOf(1, 2))
        assertNotEquals(list, listOf(3, 2, 1))
        assertNotEquals(list, listOf(1, 2, null))
        assertNotEquals(list, listOf(1L, 2L, 3L))
        assertNotEquals(list, setOf(1, 2, 3))
    }

    @Test
    fun sublistsMatchStandardListsForEveryValidRange() {
        for (size in 0..10) {
            val natural = NaturalList(size)
            val expected = List(size) { it + 1 }
            for (fromIndex in 0..size) {
                for (toIndex in fromIndex..size) {
                    val actual = natural.subList(fromIndex, toIndex)
                    assertEquals(expected.subList(fromIndex, toIndex), actual)
                    assertEquals(expected.subList(fromIndex, toIndex).hashCode(), actual.hashCode())
                }
            }
        }
    }

    @Test
    fun sublistSupportsNestedViewsAndChecksItsOwnBounds() {
        val sublist = NaturalList(6).subList(2, 5)

        assertEquals(listOf(4), sublist.subList(1, 2))
        assertThrows(IndexOutOfBoundsException::class.java) { sublist[-1] }
        assertThrows(IndexOutOfBoundsException::class.java) { sublist[3] }
        assertEquals(2, sublist.listIterator(sublist.size).previousIndex())
        assertEquals(5, sublist.listIterator(sublist.size).previous())
    }

    @Test
    fun sublistRejectsInvalidRanges() {
        val list = NaturalList(3)

        assertThrows(IndexOutOfBoundsException::class.java) { list.subList(-1, 2) }
        assertThrows(IndexOutOfBoundsException::class.java) { list.subList(0, 4) }
        assertThrows(IllegalArgumentException::class.java) { list.subList(2, 1) }
    }

    @Test
    fun emptyListIteratorHasValidCursorAndNoElements() {
        val iterator = NaturalList(0).listIterator()

        assertEquals(0, iterator.nextIndex())
        assertEquals(-1, iterator.previousIndex())
        assertFalse(iterator.hasNext())
        assertFalse(iterator.hasPrevious())
        assertThrows(NoSuchElementException::class.java) { iterator.next() }
        assertThrows(NoSuchElementException::class.java) { iterator.previous() }
    }

    @Test
    fun listIteratorMatchesStandardListAtEveryValidPosition() {
        val natural = NaturalList(5)
        val expected = listOf(1, 2, 3, 4, 5)
        for (start in 0..natural.size) {
            val actualIterator = natural.listIterator(start)
            val expectedIterator = expected.listIterator(start)
            while (expectedIterator.hasNext()) {
                assertEquals(expectedIterator.nextIndex(), actualIterator.nextIndex())
                assertEquals(expectedIterator.previousIndex(), actualIterator.previousIndex())
                assertEquals(expectedIterator.next(), actualIterator.next())
            }
            assertFalse(actualIterator.hasNext())
            while (expectedIterator.hasPrevious()) {
                assertEquals(expectedIterator.nextIndex(), actualIterator.nextIndex())
                assertEquals(expectedIterator.previousIndex(), actualIterator.previousIndex())
                assertEquals(expectedIterator.previous(), actualIterator.previous())
            }
            assertFalse(actualIterator.hasPrevious())
        }
    }

    @Test
    fun listIteratorRejectsOutOfBoundsPositions() {
        assertThrows(IndexOutOfBoundsException::class.java) { NaturalList(3).listIterator(-1) }
        assertThrows(IndexOutOfBoundsException::class.java) { NaturalList(3).listIterator(4) }
        assertThrows(IndexOutOfBoundsException::class.java) { NaturalList(0).listIterator(1) }
    }
}
