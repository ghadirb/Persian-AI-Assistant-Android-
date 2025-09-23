package com.example.persianaiapp.util

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.example.persianaiapp.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utility class for handling Persian text and RTL (Right-to-Left) layout support
 */
object PersianUtils {

    private const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"
    private const val ARABIC_DIGITS = "٠١٢٣٤٥٦٧٨٩"
    private const val ENGLISH_DIGITS = "0123456789"

    // Persian/Arabic characters that have different forms based on their position
    private const val PERSIAN_CHARS = "آابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیء"
    private const val PERSIAN_CHARS_EXTENDED = "آابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیءئؤأإةكی"

    // RTL and LTR marks
    private const val RLM = "\u200F" // Right-to-Left Mark
    private const val LRM = "\u200E" // Left-to-Right Mark
    private const val RLE = "\u202B" // Right-to-Left Embedding
    private const val LRE = "\u202A" // Left-to-Right Embedding
    private const val PDF = "\u202C" // Pop Directional Formatting
    private const val ZWJ = "\u200D" // Zero Width Joiner
    private const val ZWNJ = "\u200C" // Zero Width Non-Joiner

    // Format for Persian numbers
    private val persianNumberFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))

    init {
        // Configure decimal format for Persian numbers
        val symbols = persianNumberFormat.decimalFormatSymbols
        symbols.groupingSeparator = '،' // Persian comma
        symbols.decimalSeparator = '/'  // Persian decimal separator
        persianNumberFormat.decimalFormatSymbols = symbols
    }

    /**
     * Convert English digits to Persian digits
     */
    fun String.toPersianDigits(): String {
        if (this.isEmpty()) return this
        
        val result = StringBuilder()
        for (c in this) {
            when (c) {
                in '0'..'9' -> result.append(PERSIAN_DIGITS[c - '0'])
                ',' -> result.append('،')
                '?' -> result.append('؟')
                ';' -> result.append('؛')
                else -> result.append(c)
            }
        }
        return result.toString()
    }

    /**
     * Convert Persian digits to English digits
     */
    fun String.toEnglishDigits(): String {
        if (this.isEmpty()) return this
        
        val result = StringBuilder()
        for (c in this) {
            when (c) {
                in PERSIAN_DIGITS -> result.append(PERSIAN_DIGITS.indexOf(c))
                in ARABIC_DIGITS -> result.append(ARABIC_DIGITS.indexOf(c))
                '،' -> result.append(',')
                '؟' -> result.append('?')
                '؛' -> result.append(';')
                else -> result.append(c)
            }
        }
        return result.toString()
    }

    /**
     * Format a number with Persian digits and thousand separators
     */
    fun formatNumber(number: Number): String {
        return persianNumberFormat.format(number).toPersianDigits()
    }

    /**
     * Make text RTL by adding RLM (Right-to-Left Mark) at the end
     */
    fun String.forceRtl(): String {
        return if (TextUtils.getLayoutDirectionFromLocale(Locale("fa")) == android.util.LayoutDirection.RTL) {
            "$this$RLM"
        } else {
            this
        }
    }

    /**
     * Make text LTR by adding LRM (Left-to-Right Mark) at the end
     */
    fun String.forceLtr(): String {
        return if (TextUtils.getLayoutDirectionFromLocale(Locale.ENGLISH) == android.util.LayoutDirection.LTR) {
            "$this$LRM"
        } else {
            this
        }
    }

    /**
     * Wrap text with RLE (Right-to-Left Embedding) and PDF (Pop Directional Formatting)
     */
    fun String.wrapRtl(): String {
        return if (TextUtils.getLayoutDirectionFromLocale(Locale("fa")) == android.util.LayoutDirection.RTL) {
            "$RLE$this$PDF"
        } else {
            this
        }
    }

    /**
     * Check if text contains Persian characters
     */
    fun String.containsPersian(): Boolean {
        return any { c -> c in PERSIAN_CHARS_EXTENDED }
    }

    /**
     * Check if text is RTL (Right-to-Left)
     */
    fun String.isRtl(): Boolean {
        return when (TextUtils.getLayoutDirectionFromLocale(Locale("fa"))) {
            android.util.LayoutDirection.RTL -> true
            else -> false
        }
    }

    /**
     * Fix Persian text rendering issues (like connected letters)
     */
    fun String.fixPersianText(): String {
        if (this.isEmpty()) return this
        
        // Fix common Persian rendering issues
        var result = this
            .replace("ي", "ی") // Arabic Yeh to Persian Yeh
            .replace("ك", "ک") // Arabic Kaf to Persian Keheh
            .replace("ى", "ی") // Arabic Alef Maksura to Persian Yeh
            .replace("ه\u200cی", "ه‌ای") // Fix for words ending with ه
            .replace("\u200cها", "‌ها") // Fix for plural words
        
        // Add ZWNJ after certain prefixes
        val prefixes = listOf("می", "نمی", "بیشتر", "همین", "همان", "همین", "همان")
        for (prefix in prefixes) {
            if (result.startsWith(prefix) && result.length > prefix.length) {
                result = "$prefix${ZWNJ}${result.substring(prefix.length)}"
            }
        }
        
        return result
    }

    /**
     * Create a SpannableString with Persian font
     */
    fun Context.createPersianText(
        text: String,
        @AttrRes textAppearance: Int = android.R.attr.textAppearanceBody1,
        @FontRes fontRes: Int = R.font.vazir,
        @ColorInt textColor: Int? = null
    ): SpannableString {
        val spannable = SpannableString(text.fixPersianText())
        
        // Apply font
        val typeface = ResourcesCompat.getFont(this, fontRes)
        typeface?.let {
            spannable.setSpan(
                CustomTypefaceSpan(it),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // Apply text appearance
        val typedValue = TypedValue()
        theme.resolveAttribute(textAppearance, typedValue, true)
        spannable.setSpan(
            TextAppearanceSpan(context = this, textAppearanceResId = typedValue.resourceId),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        // Apply text color if provided
        textColor?.let {
            spannable.setSpan(
                ForegroundColorSpan(it),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        return spannable
    }

    /**
     * Create a SpannableString with mixed Persian and English text
     */
    fun createMixedText(
        context: Context,
        vararg textParts: Pair<String, Int> // Pair of text and text appearance resource
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        
        for ((text, textAppearance) in textParts) {
            val start = builder.length
            builder.append(text.fixPersianText())
            val end = builder.length
            
            // Apply text appearance
            val typedValue = TypedValue()
            context.theme.resolveAttribute(textAppearance, typedValue, true)
            builder.setSpan(
                TextAppearanceSpan(context, typedValue.resourceId),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // Apply RTL if needed
            if (text.containsPersian()) {
                builder.setSpan(
                    RtlSpan(),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        
        return builder
    }

    /**
     * Format a number with Persian digits and unit
     */
    fun formatNumberWithUnit(
        context: Context,
        number: Number,
        unit: String,
        @AttrRes unitTextAppearance: Int = android.R.attr.textAppearanceCaption
    ): SpannableString {
        val formattedNumber = formatNumber(number)
        val text = "$formattedNumber $unit"
        val spannable = SpannableString(text)
        
        // Make number bold
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            formattedNumber.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        // Apply unit text appearance
        val typedValue = TypedValue()
        context.theme.resolveAttribute(unitTextAppearance, typedValue, true)
        spannable.setSpan(
            TextAppearanceSpan(context, typedValue.resourceId),
            formattedNumber.length + 1,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        // Make unit slightly smaller
        spannable.setSpan(
            RelativeSizeSpan(0.85f),
            formattedNumber.length + 1,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        return spannable
    }

    /**
     * Fix text direction for mixed RTL and LTR content
     */
    fun fixTextDirection(text: String): String {
        if (!text.containsPersian()) {
            return text
        }
        
        // Split by words
        val words = text.split("\\s+".toRegex())
        val result = StringBuilder()
        
        for (word in words) {
            if (word.containsPersian()) {
                // Add RLE and PDF for Persian words
                result.append("$RLE$word$PDF ")
            } else {
                // Add LRM and PDF for non-Persian words
                result.append("$LRM$word$PDF ")
            }
        }
        
        return result.toString().trim()
    }

    /**
     * Check if the current locale is Persian
     */
    fun isPersianLocale(context: Context): Boolean {
        val locale = context.resources.configuration.locales[0] ?: return false
        return locale.language == "fa" || locale.language == "fa_IR" || locale.language == "fa_AF"
    }

    /**
     * Get the appropriate font based on the current locale
     */
    @FontRes
    fun getAppropriateFont(context: Context): Int {
        return if (isPersianLocale(context)) {
            R.font.vazir
        } else {
            android.R.font.sans_serif
        }
    }
}
