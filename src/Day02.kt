val maxAllowed = mapOf(
    "red" to 12,
    "green" to 13,
    "blue" to 14,
)

data class GameRound(
    val red: Int,
    val blue: Int,
    val green: Int,
)

data class Game(
    val id: Int,
    val redMax: Int,
    val blueMax: Int,
    val greenMax: Int,
)

fun String.parseGame(): Game {
    val gameId = substringAfter("Game ").substringBefore(":").toInt()
    val games = substringAfter(": ").split("; ").map {
        val redCount = Regex("[0-9]+ \\b(red)\\b").find(it)?.value?.substringBefore(" ")?.toIntOrNull()
        val greenCount = Regex("[0-9]+ \\b(green)\\b").find(it)?.value?.substringBefore(" ")?.toIntOrNull()
        val blueCount = Regex("[0-9]+ \\b(blue)\\b").find(it)?.value?.substringBefore(" ")?.toIntOrNull()
        GameRound(
            red = redCount ?: 0,
            green = greenCount ?: 0,
            blue = blueCount ?: 0,
        )
    }
    return Game(
        id = gameId,
        redMax = games.maxOf { it.red },
        greenMax = games.maxOf { it.green },
        blueMax = games.maxOf { it.blue }
    )
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val game = line.parseGame()
            if (game.blueMax <= maxAllowed["blue"]!! &&
                game.redMax <= maxAllowed["red"]!! &&
                game.greenMax <= maxAllowed["green"]!!) {
                game.id
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val game = line.parseGame()
            game.blueMax * game.greenMax * game.redMax
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
