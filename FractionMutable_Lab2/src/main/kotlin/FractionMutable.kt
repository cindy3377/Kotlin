import kotlin.math.abs

class FractionMutable(numerator: Int, denominator: Int, private var sign: Int = 1) {
    init {
        require(sign == 1 || sign == -1) { "Sign must be 1 or -1" }
        require(denominator != 0) { "Denominator cannot be zero" }
    }

    private var num: Int = abs(numerator) / gcd(abs(numerator), abs(denominator))
    private var denom: Int = abs(denominator) / gcd(abs(numerator), abs(denominator))

    override fun toString(): String {
        return "${if (sign == -1) "-" else ""}$num/$denom"
    }

    fun negate() {
        this.sign = -this.sign
    }

    fun add(other: FractionMutable) {
        val newNumerator = this.sign * this.num * other.denom + other.sign * other.num * this.denom
        val newDenominator = this.denom * other.denom
        val g = gcd(newNumerator, newDenominator)

        this.num = abs(newNumerator / g)
        this.denom = abs(newDenominator / g)
        this.sign = if (newNumerator < 0) -1 else 1
    }

    fun mult(other: FractionMutable) {
        val newNumerator = this.num * other.num
        val newDenominator = this.denom * other.denom
        val g = gcd(newNumerator, newDenominator)

        this.num = abs(newNumerator) / g
        this.denom = abs(newDenominator) / g
        this.sign = this.sign * other.sign
    }

    fun div(other: FractionMutable) {
        require(other.num != 0) { "Cannot divide by zero" }
        val reciprocal = FractionMutable(other.denom, other.num, other.sign)
        this.mult(reciprocal)
    }

    fun intPart(): Int {
        return num / denom
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = a
        var y = b
        while (y != 0) {
            val temp = y
            y = x % y
            x = temp
        }
        return x
    }
}

fun main() {
    val a = FractionMutable(1, 2, -1)
    a.add(FractionMutable(1, 3))
    println(a)  // Output: -1/6

    a.mult(FractionMutable(5, 2, -1))
    println(a)  // Output: 5/12

    a.div(FractionMutable(2, 1))
    println(a)  // Output: 5/24
}
