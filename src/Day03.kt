fun Char.isSymbol(): Boolean {
    return !this.isDigit() && this != '.'
}

fun IntArray.getIfNotZero(index: Int): Int? {
    return if (getOrNull(index) != 0 && getOrNull(index) != null) {
        getOrNull(index)
    } else {
        null
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.mapIndexed { lineNumber, lineText ->
            "[0-9]+".toRegex().findAll(lineText).map {
                var foundValidNumber = false
                (it.range.first() - 1..it.range.last() + 1).forEach { index ->
                    if (
                        input.getOrNull(lineNumber - 1)?.getOrNull(index)?.isSymbol() == true ||
                        input.getOrNull(lineNumber)?.getOrNull(index)?.isSymbol() == true ||
                        input.getOrNull(lineNumber + 1)?.getOrNull(index)?.isSymbol() == true
                    ) {
                        foundValidNumber = true
                        return@forEach
                    }
                }
                if (foundValidNumber) {
                    it.value.toInt()
                } else {
                    0
                }
            }.sum()
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val numberArray: Array<IntArray> = Array(input.size) { IntArray(input[0].length) { 0 } }
        input.forEachIndexed { lineNumber, lineText ->
            "[0-9]+".toRegex().findAll(lineText).forEach { match ->
                (match.range.first..match.range.last).forEach { index ->
                    numberArray[lineNumber][index] = match.value.toInt()
                }
            }
        }

        return input.mapIndexed { x, line ->
            "[^A-Z0-9.]".toRegex().findAll(line).map {
                it.range.first()
            }.map { y ->
                val adjacentNumbers: List<Int> = listOf(
                    x - 1 to y - 1,
                    x - 1 to y,
                    x - 1 to y + 1,
                    x to y - 1,
                    x to y + 1,
                    x + 1 to y - 1,
                    x + 1 to y,
                    x + 1 to y + 1,
                ).mapNotNull {
                    val (x1, y1) = it
                    numberArray.getOrNull(x1)?.getIfNotZero(y1)
                }.distinct()

                val adjacentNumberCount = adjacentNumbers.size
                when {
                    adjacentNumberCount <= 1 -> 0
                    adjacentNumberCount == 2 -> {
                        adjacentNumbers.first() * adjacentNumbers.last()
                    }

                    adjacentNumberCount > 2 -> error("not allowed to have more than 2")
                    else -> error("should never happen")
                }
            }.sum()
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
