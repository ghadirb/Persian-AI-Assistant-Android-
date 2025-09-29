package com.example.persianaiapp.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

/**
 * Extension function to show a toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Extension function to show a toast message from a string resource
 */
fun Context.showToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    showToast(getString(messageRes), duration)
}

/**
 * Extension function to show a snackbar with a message
 */
fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    action: Pair<String, (View) -> Unit>? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    action?.let { (actionText, actionListener) ->
        snackbar.setAction(actionText, actionListener)
    }
    snackbar.show()
}

/**
 * Extension function to show a snackbar with a string resource
 */
fun View.showSnackbar(
    @StringRes messageRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    action: Pair<Int, (View) -> Unit>? = null
) {
    val snackbar = Snackbar.make(this, messageRes, duration)
    action?.let { (actionRes, actionListener) ->
        snackbar.setAction(actionRes, actionListener)
    }
    snackbar.show()
}

/**
 * Extension function to show the keyboard
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    requestFocus()
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Extension function to hide the keyboard
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Extension function to make a view visible
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Extension function to make a view invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Extension function to make a view gone
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Extension function to set visibility based on a boolean condition
 */
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

/**
 * Extension function to set click listener with debounce
 */
fun View.setDebouncedClickListener(debounceTime: Long = 600L, action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (System.currentTimeMillis() - lastClickTime < debounceTime) return
            lastClickTime = System.currentTimeMillis()
            action()
        }
    })
}

/**
 * Extension function to set a click listener that hides the keyboard before executing the action
 */
fun View.setOnClickHideKeyboard(action: () -> Unit) {
    setOnClickListener {
        hideKeyboard()
        action()
    }
}

/**
 * Extension function to set a long click listener that hides the keyboard before executing the action
 */
fun View.setOnLongClickHideKeyboard(action: () -> Boolean) {
    setOnLongClickListener {
        hideKeyboard()
        action()
    }
}

/**
 * Extension function to set a drawable to the start of a TextView/EditText
 */
fun androidx.appcompat.widget.AppCompatTextView.setDrawableStart(drawableRes: Int) {
    val drawable = context.getDrawable(drawableRes)?.apply {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    }
    setCompoundDrawablesRelative(drawable, null, null, null)
}

/**
 * Extension function to set a drawable to the end of a TextView/EditText
 */
fun androidx.appcompat.widget.AppCompatTextView.setDrawableEnd(drawableRes: Int) {
    val drawable = context.getDrawable(drawableRes)?.apply {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    }
    setCompoundDrawablesRelative(null, null, drawable, null)
}
