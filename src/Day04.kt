import kotlin.math.pow

fun String.parseCard(): Card {
    val cardNumber = substringAfter("Card").substringBefore(":").trim().toInt()
    val numbers = substringAfter(": ")
        .split(" | ")

    val winningNumbers = numbers.first()
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.trim() }
        .map { it.toInt() }
    val ownNumbers = numbers.last()
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.trim() }
        .map { it.toInt() }
    return Card(
        cardNumber,
        winningNumbers,
        ownNumbers,
    )
}

data class Card(
    val cardNr: Int,
    val winningNumbers: List<Int>,
    val ownNumbers: List<Int>,
)

val cardCount: MutableMap<Int, Int> = mutableMapOf()

fun main() {
    fun part1(input: List<String>): Int {
        return input.map {
            it.parseCard()
        }.map { card ->
            var correctCount = 0
            card.ownNumbers.forEach { myNumber ->
                card.winningNumbers.forEach { winningNumber ->
                    if (myNumber == winningNumber) {
                        correctCount++
                    }
                }
            }
            correctCount
        }.sumOf { count ->
            when (count) {
                0 -> 0
                1 -> 1
                else -> 2.0.pow(count.toDouble() - 1).toInt()
            }
        }
    }

    fun part2(input: List<String>): Int {
        input.map {
            it.parseCard()
        }.map { card ->
            cardCount[card.cardNr] = 1
            card
        }.map { card ->
            var correctCount = 0
            card.ownNumbers.forEach { myNumber ->
                card.winningNumbers.forEach { winningNumber ->
                    if (myNumber == winningNumber) {
                        correctCount++
                    }
                }
            }
            card.cardNr to correctCount
        }.forEach {
            val (cardNr, correctCount) = it
            repeat(cardCount[cardNr]!!) {
                (cardNr + 1..cardNr + correctCount).forEach { index ->
                    cardCount[index] = cardCount[index]!!.plus(1)
                }
            }
        }

        return cardCount.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
