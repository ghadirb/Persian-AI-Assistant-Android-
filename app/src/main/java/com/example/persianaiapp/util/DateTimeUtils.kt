package com.example.persianaiapp.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for handling date and time operations
 */
object DateTimeUtils {
    
    // Date and time formats
    private const val DATE_FORMAT = "yyyy/MM/dd"
    private const val TIME_FORMAT = "HH:mm"
    private const val DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm"
    private const val READABLE_DATE_FORMAT = "MMM d, yyyy"
    private const val READABLE_DATE_TIME_FORMAT = "MMM d, yyyy HH:mm"
    private const val FILE_NAME_DATE_FORMAT = "yyyyMMdd_HHmmss"
    
    // Persian calendar constants
    private val PERSIAN_MONTHS = arrayOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )
    
    private val PERSIAN_WEEKDAYS = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه",
        "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )
    
    /**
     * Get current date and time as a formatted string
     */
    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Get current date as a formatted string
     */
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Get current time as a formatted string
     */
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }
    
    /**
     * Format a date to a readable string
     */
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat(READABLE_DATE_FORMAT, Locale.getDefault())
        return sdf.format(date)
    }
    
    /**
     * Format a date and time to a readable string
     */
    fun formatDateTime(date: Date): String {
        val sdf = SimpleDateFormat(READABLE_DATE_TIME_FORMAT, Locale.getDefault())
        return sdf.format(date)
    }
    
    /**
     * Format a date to be used in file names
     */
    fun formatDateForFileName(date: Date): String {
        val sdf = SimpleDateFormat(FILE_NAME_DATE_FORMAT, Locale.getDefault())
        return sdf.format(date)
    }
    
    /**
     * Parse a date string to a Date object
     */
    fun parseDate(dateString: String): Date? {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Parse a date and time string to a Date object
     */
    fun parseDateTime(dateTimeString: String): Date? {
        return try {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            sdf.parse(dateTimeString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get the difference between two dates in days
     */
    fun getDaysBetween(startDate: Date, endDate: Date): Long {
        val diffInMillies = endDate.time - startDate.time
        return diffInMillies / (24 * 60 * 60 * 1000)
    }
    
    /**
     * Get the difference between two dates in hours
     */
    fun getHoursBetween(startDate: Date, endDate: Date): Long {
        val diffInMillies = endDate.time - startDate.time
        return diffInMillies / (60 * 60 * 1000)
    }
    
    /**
     * Get the difference between two dates in minutes
     */
    fun getMinutesBetween(startDate: Date, endDate: Date): Long {
        val diffInMillies = endDate.time - startDate.time
        return diffInMillies / (60 * 1000)
    }
    
    /**
     * Get the difference between two dates in seconds
     */
    fun getSecondsBetween(startDate: Date, endDate: Date): Long {
        val diffInMillies = endDate.time - startDate.time
        return diffInMillies / 1000
    }
    
    /**
     * Add days to a date
     */
    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }
    
    /**
     * Add hours to a date
     */
    fun addHours(date: Date, hours: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.time
    }
    
    /**
     * Add minutes to a date
     */
    fun addMinutes(date: Date, minutes: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.time
    }
    
    /**
     * Add seconds to a date
     */
    fun addSeconds(date: Date, seconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.SECOND, seconds)
        return calendar.time
    }
    
    /**
     * Convert Gregorian date to Persian date
     */
    fun toPersianDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Convert to Persian calendar
        val persianCalendar = java.util.GregorianCalendar()
        persianCalendar.set(Calendar.YEAR, year)
        persianCalendar.set(Calendar.MONTH, month - 1)
        persianCalendar.set(Calendar.DAY_OF_MONTH, day)
        
        val persianYear = persianCalendar.get(Calendar.YEAR)
        val persianMonth = persianCalendar.get(Calendar.MONTH)
        val persianDay = persianCalendar.get(Calendar.DAY_OF_MONTH)
        
        return "$persianYear/${persianMonth + 1}/$persianDay"
    }
    
    /**
     * Get Persian month name
     */
    fun getPersianMonthName(month: Int): String {
        return if (month in 1..12) {
            PERSIAN_MONTHS[month - 1]
        } else {
            ""
        }
    }
    
    /**
     * Get Persian weekday name
     */
    fun getPersianWeekdayName(dayOfWeek: Int): String {
        return if (dayOfWeek in 1..7) {
            PERSIAN_WEEKDAYS[dayOfWeek - 1]
        } else {
            ""
        }
    }
    
    /**
     * Get current time in milliseconds
     */
    fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Format milliseconds to a readable duration (e.g., "2h 30m")
     */
    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "${days}d ${hours % 24}h"
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
    
    /**
     * Format milliseconds to a countdown timer (e.g., "02:30")
     */
    fun formatCountdown(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return String.format("%02d:%02d", minutes, seconds)
    }
}
