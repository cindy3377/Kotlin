import kotlin.system.exitProcess

class Lotto {
    fun pickNDistinct(range: IntRange, n: Int): List<Int>? {
        if (n > range.count()) return null
        return range.shuffled().take(n)
    }

    fun numDistinct(list: List<Int>): Int {
        return list.distinct().size
    }

    fun numCommon(list1: List<Int>, list2: List<Int>): Int {
        return list1.intersect(list2.toSet()).size
    }

    fun isLegalLottoGuess(guess: List<Int>, range: IntRange = lottoRange, count: Int = n): Boolean {
        return guess.size == count && guess.distinct().size == count && guess.all { it in range }
    }

    fun checkGuess(guess: List<Int>, secret: List<Int> = secretNumbers): Int {
        return if (isLegalLottoGuess(guess)) {
            numCommon(guess, secret)
        } else {
            0
        }
    }

    companion object {
        val lottoRange = 1..40 // default lotto range
        val secretNumbers = Lotto().pickNDistinct(lottoRange, 7)!!
        const val n = 7
    }
}


// 1. Reads n distinct integers from the console, validates if they are in the range [low, high]
fun readNDistinct(low: Int, high: Int, n: Int): List<Int> {
    while (true) {
        println("Give $n numbers from $low to $high, separated by commas:")
        val input = readLine() ?: continue
        val numbers = input.split(",").map { it.trim().toIntOrNull() }.filterNotNull()

        if (numbers.size == n && numbers.distinct().size == n && numbers.all { it in low..high }) {
            return numbers
        } else {
            println("Invalid input. Make sure you provide $n distinct integers between $low and $high.")
        }
    }
}

// 2. Play the lotto game
fun playLotto() {
    val lotto = Lotto()
    var continuePlaying = true

    while (continuePlaying) {
        val secretNumbers = lotto.pickNDistinct(1..40, 7)!!
        println("Secret lotto numbers generated!")

        val userGuess = readNDistinct(1, 40, 7)

        val correctCount = lotto.checkGuess(userGuess, secretNumbers)
        println("Lotto numbers: $secretNumbers, you got $correctCount correct.")

        println("More? (Y/N):")
        val answer = readLine()?.trim()?.uppercase() ?: "N"
        if (answer != "Y") {
            continuePlaying = false
        }
    }
    println("Thanks for playing!")
}

fun findLotto(lotto: Lotto): Pair<Int, List<Int>> {
    val range = 1..40
    var attempts = 0
    var guess: List<Int>
    var correctCount = 0

    do {
        guess = lotto.pickNDistinct(range, 7)!!
        correctCount = lotto.checkGuess(guess)
        attempts++
    } while (correctCount < 7)

    return Pair(attempts, guess)
}

fun main() {
    playLotto()
}
