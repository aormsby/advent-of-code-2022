package utils

object Maths {
    fun gcd(a: Int, b: Int): Int {
        var a1 = a
        var b1 = b

        while (b1 != 0) {
            val tempA = a1
            a1 = b1
            b1 = tempA % b1
        }

        return a1
    }

    fun lcm(a: Int, b: Int): Int = (a / gcd(a, b)) * b
}