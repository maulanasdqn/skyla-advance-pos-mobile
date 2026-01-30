package com.skyla.pos.common

import java.text.NumberFormat
import java.util.Locale

/**
 * Converts a value in cents to a formatted currency string.
 * Example: 1599L.formatAsCurrency() returns "$15.99"
 */
fun Long.formatAsCurrency(locale: Locale = Locale.US): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(this / 100.0)
}

/**
 * Converts a value in cents to a dollar amount as Double.
 * Example: 1599L.toDollarAmount() returns 15.99
 */
fun Long.toDollarAmount(): Double {
    return this / 100.0
}

/**
 * Parses a decimal string to cents (Long).
 * Example: "15.99".parseToCents() returns 1599L
 *
 * @throws NumberFormatException if the string is not a valid decimal number.
 */
fun String.parseToCents(): Long {
    val cleaned = this.replace(",", "").replace("$", "").trim()
    val amount = cleaned.toDouble()
    return Math.round(amount * 100)
}
