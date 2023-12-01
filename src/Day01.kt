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

fun Int.orNullIfNotFound(): Int? {
    return if (this == -1) {
        null
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
            }.orNullIfNotFound() ?: Int.MAX_VALUE
            val lastDigitIndex = line.indexOfLast {
                it.isDigit()
            }.orNullIfNotFound() ?: Int.MIN_VALUE

            val firstStringMatchIndex = numbersMap.keys.minOf { number ->
                line.indexOf(number).orNullIfNotFound() ?: Int.MAX_VALUE
            }
            val lastStringMatchIndex = numbersMap.keys.maxOf { number ->
                line.lastIndexOf(number).orNullIfNotFound() ?: Int.MIN_VALUE
            }

            val firstDigit = if (firstDigitIndex <= firstStringMatchIndex) {
                line[firstDigitIndex]
            } else {
                numbersMap.minByOrNull { number ->
                    line.indexOf(number.key).orNullIfNotFound() ?: Int.MAX_VALUE
                }!!.value
            }

            val lastDigit = if (lastDigitIndex >= lastStringMatchIndex) {
                line[lastDigitIndex]
            } else {
                numbersMap.maxByOrNull { number ->
                    line.lastIndexOf(number.key).orNullIfNotFound() ?: Int.MIN_VALUE
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
