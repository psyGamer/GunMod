package dev.psygamer.gunmod.util.math

import kotlin.math.pow

// Exponent Formula: https://stackoverflow.com/a/20984477/16204736

infix fun Short.pow(exp: Short): Short {
	if (exp == 0.toShort()) return 1.toShort()
	if (exp == 1.toShort()) return this
	
	return if (exp % 2 == 0)
		(this * this).toShort() pow (exp / 2).toShort() // Even: a = (a^2) ^ b/2
	else
		(((this * this).toShort() pow (exp / 2).toShort()) * this).toShort() // Odd: a = a * (a^2) ^ b/2
}

infix fun Int.pow(exp: Int): Int {
	if (exp == 0) return 1
	if (exp == 1) return this
	
	return if (exp % 2 == 0)
		(this * this) pow (exp / 2) // Even: a = (a^2) ^ b/2
	else
		this * (this * this) pow (exp / 2) // Odd: a = a * (a^2) ^ b/2
}

infix fun Long.pow(exp: Long): Long {
	if (exp == 0L) return 1L
	if (exp == 1L) return this
	
	return if (exp % 2 == 0L)
		(this * this) pow (exp / 2L) // Even: a = (a^2) ^ b/2
	else
		this * (this * this) pow (exp / 2) // Odd: a = a * (a^2) ^ b/2
}

infix fun Float.pow(exp: Int): Float {
	return this.toDouble().pow(exp).toFloat()
}

infix fun Float.pow(exp: Float): Float {
	return this.toDouble().pow(exp.toDouble()).toFloat()
}