import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FractionTest {

    @Test
    fun testCons() {
        val a = Fraction(2, 4, -1)
        assertEquals("-1/2", a.toString())  // Check if the fraction is reduced and has the correct sign
    }

    @Test
    fun testToString() {
        val a = Fraction(1, 2, -1)
        assertEquals("-1/2", a.toString())  // Test string representation of the fraction
    }

    @Test
    fun negate() {
        val a = Fraction(1, 2, -1)
        val negated = -a  // Negate the fraction
        assertEquals("1/2", negated.toString())  // Check the negated value
    }

    @Test
    fun addPos1() {
        val a = Fraction(1, 2)
        val result = a + Fraction(1, 3)
        assertEquals("5/6", result.toString())  // Test addition of positive fractions
    }

    @Test
    fun addPosNeg1() {
        val a = Fraction(1, 2)
        val result = a + Fraction(1, 3, -1)
        assertEquals("1/6", result.toString())  // Test addition of positive and negative fractions
    }

    @Test
    fun multPos() {
        val a = Fraction(1, 2)
        val result = a * Fraction(1, 3)
        assertEquals("1/6", result.toString())  // Test multiplication of positive fractions
    }

    @Test
    fun multPosNeg1() {
        val a = Fraction(1, 2)
        val result = a * Fraction(1, 3, -1)
        assertEquals("-1/6", result.toString())  // Test multiplication of positive and negative fractions
    }

    @Test
    fun div() {
        val a = Fraction(8, 3)
        val result = a / Fraction(4, 6)
        assertEquals("4/1", result.toString())  // Test division of fractions
    }

    @Test
    fun testAdditionWithNegativeResult() {
        val a = Fraction(1, 6)
        val result = -a + Fraction(1, 2)
        assertEquals("1/3", result.toString())  // Test addition with negation
    }

    @Test
    fun testMultiplication() {
        val a = Fraction(2, 3)
        val result = a * Fraction(3, 2)
        assertEquals("1/1", result.toString())  // Test multiplication resulting in 1/1
    }

    @Test
    fun testComparison() {
        val a = Fraction(1, 2)
        val b = Fraction(2, 3)
        assertTrue(a < b)  // Test comparison
    }
}
