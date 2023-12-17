import kotlin.math.absoluteValue

private enum class Space(private val char: Char) {
    EMPTY_SPACE('.'),
    GALAXY('#');

    override fun toString(): String {
        return char.toString()
    }

    companion object {
        fun fromChar(char: Char): Space {
            return entries.find { it.char == char }!!
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val image = input.parseImage()
        val increasedImage = image.increaseImage()
        increasedImage.print()
        val galaxies = increasedImage.findGalaxies()
        val pairs = galaxies.findPairs()

        return pairs.sumOf {
            val (xFirst, yFirst) = it.first
            val (xSecond, ySecond) = it.second

            // Manhattan distance
            (xFirst-xSecond).absoluteValue + (yFirst-ySecond).absoluteValue
        }
    }

    fun part2(input: List<String>, isTestData: Boolean = false): Long {
        val image = input.parseImage()
        val rowIndices = image.emptyRowIndices()
        val columnIndices = image.emptyColumnIndices()
        val galaxies = image.findGalaxies()
        val pairs = galaxies.findPairs()

        val multiplicator = if (isTestData) {
            100L
        } else {
            1_000_000L
        }

        return pairs.sumOf { pair ->
            val (xFirst, yFirst) = pair.first
            val (xSecond, ySecond) = pair.second

            val effectedRows = (minOf(xFirst, xSecond)..maxOf(xFirst, xSecond)).count {
                rowIndices.contains(it)
            }
            val effectedColumns = (minOf(yFirst, ySecond)..maxOf(yFirst, ySecond)).count {
                columnIndices.contains(it)
            }

            // Manhattan distance
            (xFirst-xSecond).absoluteValue + (yFirst-ySecond).absoluteValue +
                    effectedRows * multiplicator - effectedRows +
                    effectedColumns * multiplicator - effectedColumns
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)
    check(part2(testInput, isTestData = true) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}

private fun Set<Coordinates>.findPairs(): Set<Pair<Coordinates, Coordinates>> {
    val set = mutableSetOf<Pair<Coordinates, Coordinates>>()
    forEach { a ->
        forEach { b ->
            if (a != b && !set.contains(a to b) && !set.contains(b to a)) {
                set.add(a to b)
            }
        }
    }
    return set
}

private data class Coordinates(
    val x: Int,
    val y: Int,
)

private fun Array<Array<Space>>.findGalaxies(): Set<Coordinates> {
    val galaxies = mapIndexed { x, row ->
        row.mapIndexedNotNull { y, column ->
            if (column == Space.GALAXY) {
                Coordinates(x, y)
            } else {
                null
            }
        }.toSet()
    }.flatten().toSet()
    return galaxies
}

inline fun <reified T>Array<Array<T>>.transpose(): Array<Array<T>> {
    return Array(this[0].size) { i -> Array(this.size) { j -> this[j][i] } }
}
private fun Array<Array<Space>>.emptyRowIndices(): List<Int> {
    return mapIndexedNotNull { index, row ->
        val isEmpty = row.all {
            it == Space.EMPTY_SPACE
        }
        if (isEmpty) {
            index
        } else {
            null
        }
    }
}

private fun Array<Array<Space>>.emptyColumnIndices(): List<Int> {
    return transpose().mapIndexedNotNull { index, column ->
        val isEmpty = column.all {
            it == Space.EMPTY_SPACE
        }
        if (isEmpty) {
            index
        } else {
            null
        }
    }
}

private fun Array<Array<Space>>.increaseImage(): Array<Array<Space>> {
    val rowIndices = emptyRowIndices()
    val columnIndices = emptyColumnIndices()

    val listWithAddedRows = toMutableList()
    rowIndices.mapIndexed { i, rowIndex ->
        listWithAddedRows.add(rowIndex + i, Array(this[0].size){ Space.EMPTY_SPACE })
    }

    val listWithAddedColumnsAndRows =
        listWithAddedRows.map { row ->
            row.toMutableList().apply {
                columnIndices.mapIndexed { i, columnIndex ->
                    add(columnIndex + i, Space.EMPTY_SPACE)
                }
            }.toTypedArray()
        }

    return listWithAddedColumnsAndRows.toTypedArray()
}

private fun Array<Array<Space>>.print() {
    forEach {
        it.toList().joinToString(" ").println()
    }
}

private fun List<String>.parseImage(): Array<Array<Space>> {
    return map {
        it.map {
            Space.fromChar(it)
        }.toTypedArray()
    }.toTypedArray()
}
