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

fun List<List<MapInput>>.getMinimumLocationAndAssociatedSeed(seeds: List<Long>): Pair<Long, Long> {
    return seeds.map { seed ->
        fold(seed) { acc, map ->
            val mapInput = map.find {
                it.sourceRange.contains(acc)
            }

            when (mapInput) {
                null -> acc
                else -> {
                    val index = acc - mapInput.sourceRange.first
                    mapInput.destinationRange.first +index
                }
            }
        } to seed
    }.minByOrNull { it.first }!!
}

fun List<LongRange>.createSeedsFromRange(): List<Long> {
    return map {
        val divisionCount = (it.last - it.first).coerceAtMost(10_000)
        val delta = ((it.last - it.first) / divisionCount).coerceAtLeast(1)

        (0..divisionCount).map { some ->
            it.first + delta * some
        }
    }.flatten()
}

fun Long.createSeedsFromEstimate(allowedRanges: List<LongRange>): List<Long> {
    val range = allowedRanges.find {
        it.contains(this)
    }!!
    val min = (this - 100_000).coerceAtLeast(range.first)
    val max = this
    return (min..max).filter { num ->
        allowedRanges.find {
            it.contains(num)
        } != null
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val seeds = input.parseSeeds()
        return input.parseMaps().getMinimumLocationAndAssociatedSeed(seeds).first
    }

    fun part2(input: List<String>): Long {
        val startTime = System.currentTimeMillis()
        val seedRange = input.parseSeedsAsRange()
        val seeds = seedRange.createSeedsFromRange()
        val maps = input.parseMaps()
        val (location, seed) = maps.getMinimumLocationAndAssociatedSeed(seeds)
        println("best estimated location: $location | best estimated seed: $seed")

        val generatedSeeds = seed.createSeedsFromEstimate(seedRange)
        val (bestLocation, _) = maps.getMinimumLocationAndAssociatedSeed(generatedSeeds)
        println("Execution time ${((System.currentTimeMillis() - startTime) / 1000f)} seconds")
        return bestLocation
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
