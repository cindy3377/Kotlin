class Fraction(private val numerator: Int, private val denominator: Int, private val sign: Int = 1) : Comparable<Fraction> {

    init {
        require(denominator != 0) { "Denominator cannot be zero." }
    }

    private fun normalize(): Fraction {
        var num = numerator
        var den = denominator
        var finalSign = sign

        if (den < 0) {
            num = -num
            den = -den
        }
        if (num < 0) {
            finalSign = -finalSign
            num = -num
        }

        val gcd = gcd(num, den)
        return Fraction(num / gcd, den / gcd, finalSign)
    }

    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    override fun toString(): String {
        val normalized = this.normalize()
        val absNumerator = if (normalized.sign < 0) -normalized.numerator else normalized.numerator
        return "$absNumerator/${normalized.denominator}"
    }

    override fun compareTo(other: Fraction): Int {
        val a = this.numerator * other.denominator * this.sign
        val b = other.numerator * this.denominator * other.sign
        return a.compareTo(b)
    }

    operator fun plus(other: Fraction): Fraction {
        val num = this.numerator * other.denominator * this.sign + other.numerator * this.denominator * other.sign
        val den = this.denominator * other.denominator
        return Fraction(num, den).normalize()
    }

    operator fun minus(other: Fraction): Fraction {
        return this + -other
    }

    operator fun times(other: Fraction): Fraction {
        val num = this.numerator * other.numerator * this.sign * other.sign
        val den = this.denominator * other.denominator
        return Fraction(num, den).normalize()
    }

    operator fun div(other: Fraction): Fraction {
        require(other.numerator != 0) { "Cannot divide by zero." }
        return this * Fraction(other.denominator, other.numerator, other.sign)
    }

    operator fun unaryMinus(): Fraction {
        return Fraction(this.numerator, this.denominator, -this.sign).normalize()
    }

    fun add(other: Fraction): Fraction = this + other
    fun mult(other: Fraction): Fraction = this * other
}

fun main() {
    val a = Fraction(1, 2, -1)
    println(a)  // Output: -1/2
    println(a.add(Fraction(1, 3)))  // Output: -1/6
    println(a.mult(Fraction(5, 2, -1)))  // Output: 5/4
    println(a.div(Fraction(2, 1, -1)))  // Output: 1/4
    println(-Fraction(1, 6) + Fraction(1, 2))  // Output: 1/3
    println(Fraction(2, 3) * Fraction(3, 2))  // Output: 1/1
    println(Fraction(1, 2) > Fraction(2, 3))  // Output: false
}
