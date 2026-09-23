package com.example.shared.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class YearMonth(
    val year: Int,
    val month: Int
) {
    val displayLabel: String
        get() {
            val monthName = FRENCH_MONTHS[month.coerceIn(0, 11)]
            return "${monthName.replaceFirstChar { it.titlecase() }} $year"
        }

    fun previous(): YearMonth = if (month == 0) YearMonth(year - 1, 11) else YearMonth(year, month - 1)

    fun next(): YearMonth = if (month == 11) YearMonth(year + 1, 0) else YearMonth(year, month + 1)

    fun containsTimestamp(timestamp: Long): Boolean {
        val localDateTime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.year == year && localDateTime.monthNumber - 1 == month
    }

    companion object {
        private val FRENCH_MONTHS = listOf(
            "janvier", "février", "mars", "avril", "mai", "juin",
            "juillet", "août", "septembre", "octobre", "novembre", "décembre"
        )

        fun current(): YearMonth {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return YearMonth(now.year, now.monthNumber - 1)
        }

        fun fromTimestamp(timestamp: Long): YearMonth {
            val localDateTime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
            return YearMonth(localDateTime.year, localDateTime.monthNumber - 1)
        }
    }
}
