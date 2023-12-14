private fun List<String>.parseInstructions(): String {
    return this.first()
}
private fun List<String>.parseMap(): Map<String, Pair<String, String>> {
    return this.subList(2, this.size).associate {
        it.substringBefore(" =") to
                (it.substringAfter("= (").substringBefore(",") to it.substringAfter(", ").substringBefore(")"))
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val instructions = input.parseInstructions()
        val map = input.parseMap()

        var location = "AAA"
        var steps = 0
        var instructionPosition = 0
        while (location != "ZZZ") {
            steps++
            if (instructionPosition == instructions.length) {
                instructionPosition = 0
            }
            location = if (instructions[instructionPosition] == 'L') {
                map[location]!!.first
            } else {
                map[location]!!.second
            }
            instructionPosition++
        }

        return steps
    }

    fun part2(input: List<String>): Long {
        val instructions = input.parseInstructions()
        val map = input.parseMap()

        return map.keys.filter { it.endsWith("A") }.toMutableList().map { loc ->
            var location = loc
            var steps = 0L
            var instructionPosition = 0
            while (!location.endsWith("Z")) {
                steps++
                if (instructionPosition == instructions.length) {
                    instructionPosition = 0
                }
                location = if (instructions[instructionPosition] == 'L') {
                    map[location]!!.first
                } else {
                    map[location]!!.second
                }
                instructionPosition++
            }
            steps
        }.leastCommonMultiple()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 2)
    val testInput2 = readInput("Day08_test2")
    check(part2(testInput2) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

/**
 * Algorithm:
 * Least common multiple
 * by using the greatest common divisor
 *
 * Sources:
 * https://en.wikipedia.org/wiki/Least_common_multiple#Using_the_greatest_common_divisor
 * https://stackoverflow.com/a/4202114/5155371
 */
private fun List<Long>.leastCommonMultiple(): Long {
    var result = this[0]
    (1 until this.size).forEach { i ->
        result = leastCommonMultiple(result, this[i])
    }
    return result
}

private fun leastCommonMultiple(a: Long, b: Long): Long {
    return a * (b / greatestCommonDivisor(a, b))
}

private fun greatestCommonDivisor(inputA: Long, inputB: Long): Long {
    var a = inputA
    var b = inputB
    while (b > 0) {
        val temp = b
        b = a % b
        a = temp
    }
    return a
}
