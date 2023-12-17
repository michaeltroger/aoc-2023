import kotlin.math.absoluteValue

enum class Pipe {
    VERTICAL,
    HORIZONTAL,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_WEST,
    SOUTH_EAST,
    STARTING_POSITION,
    NO_PIPE,
}

fun main() {

    val characterToPipeMap = mapOf(
        '|' to Pipe.VERTICAL,
        '-' to Pipe.HORIZONTAL,
        'L' to Pipe.NORTH_EAST,
        'J' to Pipe.NORTH_WEST,
        '7' to Pipe.SOUTH_WEST,
        'F' to Pipe.SOUTH_EAST,
        '.' to Pipe.NO_PIPE,
        'S' to Pipe.STARTING_POSITION,
    )

    val pipeToCharacterMap = characterToPipeMap.map { (k, v) -> v to k }.toMap()

    val pipeToDrawableCharacter = mapOf(
        Pipe.VERTICAL to '│',
        Pipe.HORIZONTAL to '─',
        Pipe.NORTH_EAST to '└',
        Pipe.NORTH_WEST to '┘',
        Pipe.SOUTH_WEST to '┐',
        Pipe.SOUTH_EAST to '┌',
        Pipe.NO_PIPE to '.',
        Pipe.STARTING_POSITION to 'S',
    )

    data class Coordinates(
        val x: Int,
        val y: Int,
    )
    data class Vertex(
        val coordinates: Coordinates,
        val data: Pipe,
    ) {
        val char: Char
            get() = pipeToCharacterMap[data]!!

        val drawableChar: Char
            get() = pipeToDrawableCharacter[data]!!

        override fun toString(): String {
            return "${coordinates.x}/${coordinates.y} $data $char"
        }
    }

    fun addToMap(adjacencyMap: MutableMap<Vertex, List<Vertex>>, data: Vertex, input: Array<Array<Vertex>>) {
        val x = data.coordinates.x
        val y = data.coordinates.y
        val neighbors = when(data.data) {
            Pipe.VERTICAL -> Coordinates(x - 1, y) to Coordinates(x + 1, y)
            Pipe.HORIZONTAL -> Coordinates(x, y - 1) to Coordinates(x, y + 1)
            Pipe.NORTH_EAST -> Coordinates(x - 1, y) to Coordinates(x, y + 1)
            Pipe.NORTH_WEST -> Coordinates(x - 1, y) to Coordinates(x, y - 1)
            Pipe.SOUTH_WEST -> Coordinates(x + 1, y) to Coordinates(x, y - 1)
            Pipe.SOUTH_EAST -> Coordinates(x + 1, y) to Coordinates(x, y + 1)
            Pipe.STARTING_POSITION -> Coordinates(-1, -1) to Coordinates(-1, -1)
            Pipe.NO_PIPE -> Coordinates(-1, -1) to Coordinates(-1, -1)
        }.let {
            val first = input.getOrNull(it.first.x)?.getOrNull(it.first.y)?.data
            val second = input.getOrNull(it.second.x)?.getOrNull(it.second.y)?.data
            if (first == null || second == null) {
                emptyList()
            } else {
                listOf(Vertex(it.first, first), Vertex(it.second, second))
            }
        }
        adjacencyMap[data] = neighbors
    }

    fun printAnimal(input: List<String>, animal: List<Vertex>) {
        val animalDrawing = Array(input.size) { Array(input.first().length) { ' ' }}.also { drawing ->
            animal.forEach {
                drawing[it.coordinates.x][it.coordinates.y] = it.drawableChar
            }
        }
        animalDrawing.forEach {
            println(it.joinToString(" "))
        }
    }

    fun parseAnimal(input: List<String>): MutableList<Vertex> {
        val map = input.mapIndexed { x, line ->
            line.toCharArray().mapIndexed { y, char ->
                Vertex(Coordinates(x, y), characterToPipeMap[char]!!)
            }.toTypedArray()
        }.toTypedArray()

        val adjacencyMap: MutableMap<Vertex, List<Vertex>> = mutableMapOf()
        map.forEach {
            it.forEach {
                addToMap(adjacencyMap, it, map)
            }
        }
        var startX = 0
        var startY = 0
        map.forEachIndexed { x, line ->
            val y = line.indexOfFirst { it.data == Pipe.STARTING_POSITION }
            if (y != -1) {
                startX = x
                startY = y
                return@forEachIndexed
            }
        }
        val startCoordinates = Vertex(Coordinates(startX, startY), Pipe.STARTING_POSITION)
        val neighborsStart: MutableList<Vertex> = mutableListOf()
        adjacencyMap.forEach {
            if (it.value.find { it.data == Pipe.STARTING_POSITION } != null) {
                neighborsStart.add(it.key)
            }
        }
        val first = neighborsStart.first()
        val last = neighborsStart.last()

        var previousLocation: Vertex = startCoordinates
        var currentLocation: Vertex = first
        val animal = mutableListOf(startCoordinates)
        while (currentLocation != last) {
            animal.add(currentLocation)
            val firstNeighbor = adjacencyMap[currentLocation]!!.first()
            val secondNeighbor = adjacencyMap[currentLocation]!!.last()
            if (firstNeighbor != previousLocation && firstNeighbor != startCoordinates) {
                previousLocation = currentLocation
                currentLocation = firstNeighbor
            } else if (secondNeighbor != previousLocation && secondNeighbor != startCoordinates) {
                previousLocation = currentLocation
                currentLocation = secondNeighbor
            }
        }
        animal.add(currentLocation)
        animal.add(startCoordinates)
        return animal
    }

    fun shoelaceAlgorithm(data: List<Vertex>): Int {
        val area = (0..data.size-2).sumOf { i->
            data[i].coordinates.x * data[i+1].coordinates.y -
                    data[i+1].coordinates.x * data[i].coordinates.y
        }.div(2).absoluteValue
        return area - data.size.div(2).minus(1)
    }

    fun part1(input: List<String>): Int {
        val animal = parseAnimal(input)
        return animal.size/2
    }

    fun part2(input: List<String>): Int {
        val animal = parseAnimal(input)
        printAnimal(input, animal)

        return shoelaceAlgorithm(animal)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
    val testInput2 = readInput("Day10_test2")
    check(part2(testInput2) == 10)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
