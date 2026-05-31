package com.pausiar.fit.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Date helpers. Dates are stored as ISO `yyyy-MM-dd` strings for easy sorting. */
object DateUtils {

    val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val prettyFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "ES"))

    private val shortFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "ES"))

    fun today(): LocalDate = LocalDate.now()

    fun toIso(date: LocalDate): String = date.format(isoFormatter)

    fun parse(iso: String): LocalDate = LocalDate.parse(iso, isoFormatter)

    /** ISO day-of-week (Monday = 1 ... Sunday = 7). */
    fun isoDayOfWeek(date: LocalDate = today()): Int = date.dayOfWeek.value

    /** Monday of the week containing [date]. */
    fun startOfWeek(date: LocalDate = today()): LocalDate =
        date.with(DayOfWeek.MONDAY)

    fun endOfWeek(date: LocalDate = today()): LocalDate =
        startOfWeek(date).plusDays(6)

    /** The concrete date for a given ISO [dayOfWeek] within the week of [reference]. */
    fun dateForDayOfWeek(dayOfWeek: Int, reference: LocalDate = today()): LocalDate =
        startOfWeek(reference).plusDays((dayOfWeek - 1).toLong())

    fun prettyDate(date: LocalDate): String =
        date.format(prettyFormatter).replaceFirstChar { it.uppercase() }

    fun shortDate(date: LocalDate): String = date.format(shortFormatter)

    fun daysBetween(a: LocalDate, b: LocalDate): Long =
        ChronoUnit.DAYS.between(a, b)
}
