data class RaceRecord(
    val time: Long,
    val distance: Long,
)

fun List<String>.parseRecords(): List<RaceRecord> {
    val timeList = this.first()
        .removePrefix("Time:")
        .split(" ")
        .filter {
            it.isNotBlank()
        }.map {
            it.trim()
        }.map {
            it.toLong()
        }

    val distanceList = this.last()
        .removePrefix("Distance:")
        .split(" ")
        .filter {
            it.isNotBlank()
        }.map {
            it.trim()
        }.map {
            it.toLong()
        }

    return timeList.indices.map {
        RaceRecord(
            time = timeList[it],
            distance = distanceList[it],
        )
    }
}

fun List<String>.parseRecordsAsSingleNumber(): RaceRecord {
    val time = this.first()
        .removePrefix("Time:")
        .trim()
        .filter {
            it != ' '
        }.toLong()

    val distance = this.last()
        .removePrefix("Distance:")
        .trim()
        .filter {
            it != ' '
        }.toLong()

    return RaceRecord(
        time = time,
        distance = distance,
    )
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.parseRecords().map { raceRecord ->
            (1..<raceRecord.time).map { holdTime ->
                val remainingTime = raceRecord.time - holdTime
                val speed = holdTime
                remainingTime * speed
            }.count { distanceTravelled ->
                distanceTravelled > raceRecord.distance
            }
        }.reduce { acc, i ->
            acc * i
        }
    }

    fun part2(input: List<String>): Int {
        val raceRecord = input.parseRecordsAsSingleNumber()
        return (1..<raceRecord.time).map { holdTime ->
            val remainingTime = raceRecord.time - holdTime
            val speed = holdTime
            remainingTime * speed
        }.count { distanceTravelled ->
            distanceTravelled > raceRecord.distance
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
