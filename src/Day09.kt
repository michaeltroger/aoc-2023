fun main() {
    fun List<String>.parseSensorData(): List<List<Int>> {
        return this.map {
            it.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        }
    }

    fun getNextValue(input: List<Int>): Int {
        val list = input.zipWithNext { a, b ->
            b - a
        }
        val numberVariation = list.groupingBy { it }.eachCount()
        return if (numberVariation.size == 1 && numberVariation.contains(0)) {
            0
        } else {
            list.last() + getNextValue(list)
        }
    }

    fun getPreviousValue(input: List<Int>): Int {
        val list = input.zipWithNext { a, b ->
            b - a
        }
        val numberVariation = list.groupingBy { it }.eachCount()
        return if (numberVariation.size == 1 && numberVariation.contains(0)) {
            0
        } else {
            list.first() - getPreviousValue(list)
        }
    }

    fun part1(input: List<String>): Int {
        return input.parseSensorData().sumOf {
            it.last() + getNextValue(it)
        }
    }

    fun part2(input: List<String>): Int {
        return input.parseSensorData().sumOf {
            it.first() - getPreviousValue(it)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
