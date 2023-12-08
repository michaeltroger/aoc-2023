import kotlin.math.max

val rankingPart1 = mapOf(
    'A' to 12,
    'K' to 11,
    'Q' to 10,
    'J' to 9,
    'T' to 8,
    '9' to 7,
    '8' to 6,
    '7' to 5,
    '6' to 4,
    '5' to 3,
    '4' to 2,
    '3' to 1,
    '2' to 0,
)

val rankingPart2 = mapOf(
    'A' to 12,
    'K' to 11,
    'Q' to 10,
    'T' to 9,
    '9' to 8,
    '8' to 7,
    '7' to 6,
    '6' to 5,
    '5' to 4,
    '4' to 3,
    '3' to 2,
    '2' to 1,
    'J' to 0,
)

data class Strength(
    val generalStrength: Long,
    val detailedStrength: Long,
)

data class Hand(
    val cards: String,
    val bid: Long,
)

fun List<String>.parseHands(): List<Hand> {
    val cards = map {
        it.substring(startIndex = 0, endIndex = 5)
    }
    val bids = map {
        it.substringAfter(" ").trim().toLong()
    }
    return bids.indices.map {
        Hand(
            cards = cards[it],
            bid = bids[it],
        )
    }
}

fun Hand.getStrength(): Long {
    val cardCombinations = cards.groupingBy {
        it
    }.eachCount()

    return when {
        cardCombinations.size == 1 -> { // five of a kind
            7
        }

        cardCombinations.size == 2 &&
                (cardCombinations.values.toList()[0] == 4 ||
                        cardCombinations.values.toList()[1] == 4)
        -> { // four of a kind
            6
        }

        cardCombinations.size == 2 &&
                (cardCombinations.values.toList()[0] == 3 ||
                        cardCombinations.values.toList()[1] == 3)
        -> { // full house
            5
        }

        cardCombinations.size == 3 &&
                (cardCombinations.values.toList()[0] == 3 ||
                        cardCombinations.values.toList()[1] == 3 ||
                        cardCombinations.values.toList()[2] == 3)
        -> { // three of a kind
            4
        }

        cardCombinations.size == 3 &&
                (cardCombinations.values.toList()[0] == 2 ||
                        cardCombinations.values.toList()[1] == 2 ||
                        cardCombinations.values.toList()[2] == 2)
        -> { // two pair
            3
        }

        cardCombinations.size == 4 &&
                (cardCombinations.values.toList()[0] == 2 ||
                        cardCombinations.values.toList()[1] == 2 ||
                        cardCombinations.values.toList()[2] == 2 ||
                        cardCombinations.values.toList()[3] == 2)
        -> { // one pair
            2
        }

        cardCombinations.size == 5 -> { // high card
            1
        }

        else -> error("should never happen")
    }.toLong()
}

fun Hand.getDetailedStrength(isPart2: Boolean): Long {
    return cards.toList().mapIndexed { index, c ->
        val multiplier = when(index) {
            0 -> 100000000L
            1 -> 1000000L
            2 -> 10000L
            3 -> 100L
            4 -> 1L
            else -> error ("should never happen")
        }
        if (isPart2) {
            rankingPart2[c]!!.times(multiplier)
        } else {
            rankingPart1[c]!!.times(multiplier)
        }
    }.sum()
}

fun Hand.getJokerCount(): Int {
    return cards.groupingBy {
        it
    }.eachCount()['J'] ?: 0
}

fun Char.isJoker(): Boolean {
    return this == 'J'
}

fun StringBuilder.toHand(): Hand {
    return Hand(cards = this.toString(), bid = 0)
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.parseHands().map { hand ->
            hand to Strength(
                generalStrength = hand.getStrength(),
                detailedStrength = hand.getDetailedStrength(isPart2 = false)
            )
        }.sortedWith(
            compareBy<Pair<Hand, Strength>> { it.second.generalStrength }.thenBy { it.second.detailedStrength }
        ).mapIndexed { index, pair ->
            pair.first to index + 1
        }.sumOf {
            it.first.bid * it.second
        }
    }

    fun part2(input: List<String>): Long {
        return input.parseHands().map { hand ->
            val jokerCount = hand.getJokerCount()
            val jokerIndices = hand.cards.mapIndexed { index, c ->
                if (c.isJoker()) {
                    index
                } else {
                    -1
                }
            }.filter { it != -1 }
            val allCardTypes = rankingPart2.keys.toList()
            val generalStrength = when (jokerCount) {
                5 -> {
                    7
                }
                4 -> {
                    7
                }
                0 -> {
                    hand.getStrength()
                }
                3 -> {
                    var bestHandScore = 0
                    allCardTypes.indices.forEach { x->
                        allCardTypes.indices.forEach { y ->
                            allCardTypes.indices.forEach { z ->
                                val modifiedHand = StringBuilder(hand.cards).apply {
                                    setCharAt(jokerIndices[0], allCardTypes[x])
                                    setCharAt(jokerIndices[1], allCardTypes[y])
                                    setCharAt(jokerIndices[2], allCardTypes[z])
                                }.toHand()
                                bestHandScore = max(modifiedHand.getStrength().toDouble(), bestHandScore.toDouble()).toInt()
                            }
                        }
                    }
                    bestHandScore
                }
                2 -> {
                    var bestHandScore = 0
                    allCardTypes.indices.forEach { x->
                        allCardTypes.indices.forEach { y ->
                            val modifiedHand = StringBuilder(hand.cards).apply {
                                setCharAt(jokerIndices[0], allCardTypes[x])
                                setCharAt(jokerIndices[1], allCardTypes[y])
                            }.toHand()
                            bestHandScore = max(modifiedHand.getStrength().toDouble(), bestHandScore.toDouble()).toInt()
                        }
                    }
                    bestHandScore
                }
                1 -> {
                    var bestHandScore = 0
                    allCardTypes.indices.forEach { x->
                        val modifiedHand = StringBuilder(hand.cards).apply {
                            setCharAt(jokerIndices[0], allCardTypes[x])
                        }.toHand()
                        bestHandScore = max(modifiedHand.getStrength().toDouble(), bestHandScore.toDouble()).toInt()
                    }
                    bestHandScore
                }
                else -> error("should never happen")
            }.toLong()

            hand to Strength(
                generalStrength = generalStrength,
                detailedStrength = hand.getDetailedStrength(isPart2 = true)
            )
        }.sortedWith(
            compareBy<Pair<Hand, Strength>> { it.second.generalStrength }.thenBy { it.second.detailedStrength }
        ).mapIndexed { index, pair ->
            pair.first to index + 1
        }.sumOf {
            it.first.bid * it.second
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440L)
    check(part2(testInput) == 5905L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
