package com.example.persianaiapp.util

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan

/**
 * Span that forces RTL (Right-to-Left) text direction for the text it's attached to.
 * This is particularly useful for mixed RTL and LTR text.
 */
class RtlSpan : LeadingMarginSpan {
    
    override fun getLeadingMargin(first: Boolean): Int {
        return 0
    }

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        // No drawing needed, we just need the RTL behavior
    }

    override fun getLeadingMargin(first: Boolean, text: CharSequence?, lineStart: Int, lineEnd: Int, firstLine: Boolean): Int {
        return 0
    }

    companion object {
        /**
         * Check if the text at the specified position has an RtlSpan
         */
        fun hasRtlSpan(text: CharSequence, where: Int): Boolean {
            if (text !is Spanned) {
                return false
            }
            
            val spans = text.getSpans(where, where, RtlSpan::class.java)
            return spans.isNotEmpty()
        }
        
        /**
         * Check if the text contains any RTL characters
         */
        fun isRtlText(text: CharSequence): Boolean {
            if (text.isEmpty()) {
                return false
            }
            
            // Check for RTL characters in the text
            for (i in 0 until text.length) {
                val directionality = Character.getDirectionality(text[i]).toInt()
                if (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC ||
                    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING ||
                    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
                ) {
                    return true
                }
            }
            
            return false
        }
    }
}
