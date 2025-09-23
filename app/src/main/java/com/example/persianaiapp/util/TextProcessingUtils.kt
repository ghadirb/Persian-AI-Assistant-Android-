package com.example.persianaiapp.util

import android.content.Context
import java.text.Normalizer
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for text processing operations specific to Persian language
 */
@Singleton
class TextProcessingUtils @Inject constructor() {

    companion object {
        // Persian characters that have different forms based on their position
        private val PERSIAN_CHARS = "آابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیء"
        
        // Persian numbers
        private val PERSIAN_NUMBERS = "۰۱۲۳۴۵۶۷۸۹"
        
        // Arabic numbers
        private val ARABIC_NUMBERS = "٠١٢٣٤٥٦٧٨٩"
        
        // English numbers
        private val ENGLISH_NUMBERS = "0123456789"
        
        // Characters that should be replaced with space
        private val SPACE_CHARS = "\u200C\u200D\u200E\u200F\u202A-\u202E\u2066-\u2069"
        
        // Punctuation marks that should be normalized
        private val PUNCTUATION = ".,;:!?،؛؟"
        
        // Extra spaces pattern (more than one space or special space characters)
        private val EXTRA_SPACES_PATTERN = "[ \t\n\r\f\v]+".toRegex()
    }
    
    /**
     * Normalize Persian text by fixing common issues
     */
    fun normalizePersianText(text: String): String {
        if (text.isEmpty()) return text
        
        return text
            .normalizeDiacritics()
            .normalizeNumbers()
            .normalizePunctuation()
            .normalizeSpaces()
            .fixPersianCharacters()
    }
    
    /**
     * Remove diacritics from text
     */
    private fun String.normalizeDiacritics(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFC)
    }
    
    /**
     * Normalize all numbers to Persian numbers
     */
    private fun String.normalizeNumbers(): String {
        var result = this
        
        // Convert Arabic numbers to Persian
        for (i in ARABIC_NUMBERS.indices) {
            result = result.replace(ARABIC_NUMBERS[i], PERSIAN_NUMBERS[i])
        }
        
        // Convert English numbers to Persian
        for (i in ENGLISH_NUMBERS.indices) {
            result = result.replace(ENGLISH_NUMBERS[i], PERSIAN_NUMBERS[i])
        }
        
        return result
    }
    
    /**
     * Normalize punctuation marks in Persian text
     */
    private fun String.normalizePunctuation(): String {
        return this.replace("؟", "?") // Arabic question mark to standard
            .replace("،", ",")     // Arabic comma to standard
            .replace("؛", ";")     // Arabic semicolon to standard
            .replace("۔", ".")     // Arabic full stop to standard
            .replace("٪", "%")      // Arabic percent to standard
            .replace("٫", ".")      // Arabic decimal point to standard
            .replace("٬", ",")      // Arabic thousands separator to standard
    }
    
    /**
     * Normalize spaces and remove extra spaces
     */
    private fun String.normalizeSpaces(): String {
        // Replace all space-like characters with a standard space
        var result = this.replace("[\u200C\u200D\u200E\u200F\u202A-\u202E\u2066-\u2069]".toRegex(), " ")
        
        // Replace multiple spaces with a single space
        result = EXTRA_SPACES_PATTERN.replace(result, " ")
        
        // Trim leading and trailing spaces
        return result.trim()
    }
    
    /**
     * Fix common Persian character issues
     */
    private fun String.fixPersianCharacters(): String {
        var result = this
        
        // Fix common character replacements
        result = result.replace("ك", "ک") // Arabic kaf to Persian keh
            .replace("ي", "ی")  // Arabic yeh to Persian yeh
            .replace("ى", "ی")  // Arabic alef maksura to Persian yeh
            .replace("ة", "ه")  // Arabic teh marbuta to heh
            .replace("أ", "ا")  // Arabic alef with hamza above to alef
            .replace("إ", "ا")  // Arabic alef with hamza below to alef
            .replace("ؤ", "و")  // Arabic waw with hamza to waw
            .replace("ئ", "ی")  // Arabic yeh with hamza to yeh
        
        // Fix spacing around punctuation
        result = result.replace(" ?", "?")
            .replace(" ؟", "?")
            .replace(" .", ".")
            .replace(" ،", "،")
            .replace(" :", ":")
            .replace(" ؛", "؛")
        
        // Fix spacing for Persian words
        result = result.replace("هاي ", "های ")
            .replace("هاي", "هایی")
            .replace("هاي", "های")
            .replace("هاي", "هایی")
            .replace("هاي", "های")
            .replace("هاي", "هایی")
            .replace("هاي", "های")
        
        return result
    }
    
    /**
     * Check if text contains Persian characters
     */
    fun containsPersian(text: String): Boolean {
        return text.any { it in PERSIAN_CHARS }
    }
    
    /**
     * Count the number of Persian words in the text
     */
    fun countPersianWords(text: String): Int {
        if (text.isEmpty()) return 0
        
        // Split by any non-Persian character
        val words = text.split("[^\p{InARABIC}\p{InARABIC_EXT_A}]+".toRegex())
        return words.count { it.isNotBlank() }
    }
    
    /**
     * Extract Persian text from mixed content
     */
    fun extractPersianText(text: String): String {
        // Match any sequence of Persian/Arabic characters
        val pattern = "[\p{InARABIC}\p{InARABIC_EXT_A}]+".toRegex()
        val matches = pattern.findAll(text)
        return matches.joinToString(" ") { it.value }
    }
    
    /**
     * Clean and prepare text for AI processing
     */
    fun cleanTextForAI(text: String): String {
        return text.normalizePersianText()
            .replace("\n", " ")
            .replace("\r", " ")
            .replace("\t", " ")
            .replace(EXTRA_SPACES_PATTERN, " ")
            .trim()
    }
    
    /**
     * Format text for display in the UI
     */
    fun formatForDisplay(text: String): String {
        return text.normalizePersianText()
            .replace("\n\n+".toRegex(), "\n\n")
            .trim()
    }
    
    /**
     * Truncate text to a maximum length while preserving words
     */
    fun truncateText(text: String, maxLength: Int, ellipsis: String = "..."): String {
        if (text.length <= maxLength) return text
        
        var result = text.substring(0, maxLength)
        val lastSpace = result.lastIndexOf(' ')
        
        if (lastSpace > 0) {
            result = result.substring(0, lastSpace)
        }
        
        return "$result$ellipsis"
    }
    
    /**
     * Calculate reading time for Persian text (in minutes)
     */
    fun calculateReadingTime(text: String, wordsPerMinute: Int = 200): Int {
        val words = text.split("\\s+".toRegex()).size
        return (words / wordsPerMinute.toDouble()).toInt() + 1
    }
}
