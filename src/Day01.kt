val numbersMap = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

fun Int.orIntMinIfNotFound(): Int {
    return if (this == -1) {
        Int.MIN_VALUE
    } else {
        this
    }
}

fun Int.orIntMaxIfNotFound(): Int {
    return if (this == -1) {
        Int.MAX_VALUE
    } else {
        this
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val firstDigit = line.first {
                it.isDigit()
            }
            val lastDigit = line.last {
                it.isDigit()
            }
            "$firstDigit$lastDigit".toInt()
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val firstDigitIndex = line.indexOfFirst {
                it.isDigit()
            }.orIntMaxIfNotFound()
            val lastDigitIndex = line.indexOfLast {
                it.isDigit()
            }.orIntMinIfNotFound()

            fun getFirstMatchIndex(number: String): Int {
                return line.indexOf(number).orIntMaxIfNotFound()
            }
            fun getLastMatchIndex(number: String): Int {
                return line.lastIndexOf(number).orIntMinIfNotFound()
            }

            val firstStringMatchIndex = numbersMap.minOf { number ->
                getFirstMatchIndex(number.key)
            }
            val lastStringMatchIndex = numbersMap.maxOf { number ->
                getLastMatchIndex(number.key)
            }

            val firstDigit = if (firstDigitIndex <= firstStringMatchIndex) {
                line[firstDigitIndex]
            } else {
                numbersMap.minByOrNull { number ->
                    getFirstMatchIndex(number.key)
                }!!.value
            }

            val lastDigit = if (lastDigitIndex >= lastStringMatchIndex) {
                line[lastDigitIndex]
            } else {
                numbersMap.maxByOrNull { number ->
                    getLastMatchIndex(number.key)
                }!!.value
            }

            "$firstDigit$lastDigit".toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day01_test1")
    check(part1(testInput1) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
