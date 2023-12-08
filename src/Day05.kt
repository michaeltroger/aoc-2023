data class MapInput(
    val sourceRange: LongRange,
    val destinationRange: LongRange,
)

fun String.parseMap(mapName: String): List<MapInput> {
    return substringAfter("$mapName map:-")
        .substringBefore("--")
        .split("-")
        .map {
            val numbers = it.split(" ").map { numberAsString ->
                numberAsString.toLong()
            }
            MapInput(
                destinationRange = numbers[0]..<numbers[0]+numbers[2],
                sourceRange = numbers[1]..<numbers[1]+numbers[2],
            )
        }.sortedBy { it.sourceRange.first }
}

fun List<String>.parseMaps(): List<List<MapInput>> {
    val file = joinToString("-")
    return listOf(
        file.parseMap("seed-to-soil"),
        file.parseMap("soil-to-fertilizer"),
        file.parseMap("fertilizer-to-water"),
        file.parseMap("water-to-light"),
        file.parseMap("light-to-temperature"),
        file.parseMap("temperature-to-humidity"),
        file.parseMap("humidity-to-location"),
    )
}

fun List<String>.parseSeeds(): List<Long> {
    return first()
        .substringAfter(": ")
        .split(" ")
        .map { it.toLong() }
}

fun List<String>.parseSeedsAsRange(): List<LongRange> {
    val input = parseSeeds()
    val seedRanges: List<LongRange> = input.chunked(2) {
        it.first()..<it.first()+it.last()
    }

    return seedRanges
}

fun List<List<MapInput>>.getMinimumLocationAndSeed(seeds: List<Long>): Pair<Long, Long> {
    return seeds.map { s ->
        val location = fold(s) { seed, listInput ->
            val mapInput = listInput.find {
                it.sourceRange.contains(seed)
            }

            when (mapInput) {
                null -> seed
                else -> {
                    val index = seed - mapInput.sourceRange.first
                    mapInput.destinationRange.first +index
                }
            }
        }
        location to s
    }.minByOrNull { it.first }!!
}

fun List<LongRange>.createSeedsFromRange(): List<Long> {
    val divisionSteps = 100000
    return map {
        val delta = ((it.last - it.first) / divisionSteps).coerceAtLeast(1)

        (0..divisionSteps).map { some ->
            it.first + delta * some
        }
    }.flatten()
}

fun main() {
    fun part1(input: List<String>): Long {
        val seeds = input.parseSeeds()
        return input.parseMaps().getMinimumLocationAndSeed(seeds).first
    }

    fun part2(input: List<String>): Long {
        val seedRange = input.parseSeedsAsRange()
        val seeds = seedRange.createSeedsFromRange()
        val temp = input.parseMaps().getMinimumLocationAndSeed(seeds)

        val seeds1 = (temp.second-100000..temp.second+100000).toList().filter { num ->
            seedRange.find {
                it.contains(num)
             } != null
        }

        return input.parseMaps().getMinimumLocationAndSeed(seeds1).first
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
