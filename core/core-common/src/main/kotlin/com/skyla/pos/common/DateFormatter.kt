package com.skyla.pos.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Formats an ISO 8601 datetime string to a readable date string.
 * Example: "2026-01-30T14:30:00" -> "Jan 30, 2026"
 */
fun String.toReadableDate(): String {
    return try {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        try {
            val date = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
            date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e: Exception) {
            this
        }
    }
}

/**
 * Formats an ISO 8601 datetime string to a readable date and time string.
 * Example: "2026-01-30T14:30:00" -> "Jan 30, 2026 14:30"
 */
fun String.toReadableDateTime(): String {
    return try {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    } catch (e: Exception) {
        this
    }
}

/**
 * Formats a [LocalDate] as "MMM dd, yyyy".
 * Example: LocalDate.of(2026, 1, 30) -> "Jan 30, 2026"
 */
fun LocalDate.formatAsReadable(): String {
    return this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
}

/**
 * Formats a [LocalDateTime] as "MMM dd, yyyy HH:mm".
 * Example: LocalDateTime.of(2026, 1, 30, 14, 30) -> "Jan 30, 2026 14:30"
 */
fun LocalDateTime.formatAsReadableDateTime(): String {
    return this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
}

/**
 * Returns today's date as a string in "yyyy-MM-dd" format.
 */
fun getTodayDateString(): String {
    return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
}

/**
 * Returns a date range as a [Pair] of "yyyy-MM-dd" strings for the given [period].
 *
 * Supported periods:
 * - "today": start and end are both today
 * - "this_week": Monday through Sunday of the current week
 * - "this_month": first day through last day of the current month
 *
 * @return Pair of (startDate, endDate) formatted as "yyyy-MM-dd"
 * @throws IllegalArgumentException if the period is not recognized
 */
fun getDateRange(period: String): Pair<String, String> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    return when (period.lowercase()) {
        "today" -> {
            val dateStr = today.format(formatter)
            dateStr to dateStr
        }

        "this_week" -> {
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            startOfWeek.format(formatter) to endOfWeek.format(formatter)
        }

        "this_month" -> {
            val startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
            val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
            startOfMonth.format(formatter) to endOfMonth.format(formatter)
        }

        else -> throw IllegalArgumentException("Unknown period: $period. Use 'today', 'this_week', or 'this_month'.")
    }
}
