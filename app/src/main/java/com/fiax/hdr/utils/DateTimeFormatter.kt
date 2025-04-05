package com.fiax.hdr.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * An interface for formatting date and time values.
 *
 * This interface provides methods for formatting timestamps, dates, and times
 * into human-readable strings.
 */
interface DateTimeFormatter {

    /**
     * Formats a timestamp into a string representation.
     *
     * @param timestamp The timestamp to format, in milliseconds since the epoch.
     * @return The formatted timestamp string.
     */
    fun formatTimestamp(timestamp: Long): String

    /**
     * Formats a date into a string representation.
     *
     * @param timestamp The timestamp representing the date to format, in milliseconds since the epoch.
     * @return The formatted date string.
     */
    fun formatDate(timestamp: Long): String

    /**
     * Formats a time into a string representation.
     *
     * @param timestamp The timestamp representing the time to format, in milliseconds since the epoch.
     * @return The formatted time string.
     */
    fun formatTime(timestamp: Long): String
}

/**
 * An implementation of the `DateTimeFormatter` interface using `SimpleDateFormat`.
 *
 * This class provides concrete implementations for formatting timestamps, dates, and
 * times using the `SimpleDateFormat` class.
 */
class DateTimeFormatterImpl : DateTimeFormatter {

    /**
     * Formats a timestamp into a string representation using the format "dd/MM/yyyy HH:mm".
     *
     * @param timestamp The timestamp to format, in milliseconds since the epoch.
     * @return The formatted timestamp string.
     */
    override fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    /**
     * Formats a date into a string representation using the format "dd/MM/yyyy".
     *
     * @param timestamp The timestamp representing the date to format, in milliseconds since the epoch.
     * @return The formatted date string.
     */
    override fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    /**
     * Formats a time into a string representation using the format "HH:mm".
     *
     * @param timestamp The timestamp representing the time to format, in milliseconds since the epoch.
     * @return The formatted time string.
     */
    override fun formatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(timestamp)
    }
}