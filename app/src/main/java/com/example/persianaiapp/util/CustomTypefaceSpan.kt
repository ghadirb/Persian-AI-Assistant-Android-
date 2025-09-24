package com.example.persianaiapp.util

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

/**
 * Custom TypefaceSpan that allows setting a custom Typeface
 * This is particularly useful for Persian/Arabic fonts that need special rendering
 */
class CustomTypefaceSpan(private val typeface: Typeface) : TypefaceSpan("") {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeface(ds, typeface)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeface(paint, typeface)
    }

    private fun applyCustomTypeface(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0

        // Apply the typeface from the old typeface's style
        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }

        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }

        // Apply the new typeface
        paint.typeface = tf
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomTypefaceSpan

        if (typeface != other.typeface) return false

        return true
    }

    override fun hashCode(): Int {
        return typeface.hashCode()
    }
}
