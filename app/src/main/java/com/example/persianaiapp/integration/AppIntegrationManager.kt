package com.example.persianaiapp.integration

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppIntegrationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Maps Integration
    fun openMapsLocation(latitude: Double, longitude: Double, label: String? = null) {
        val uri = if (label != null) {
            Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
        } else {
            Uri.parse("geo:$latitude,$longitude")
        }
        
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to web maps
            val webUri = Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun searchMapsLocation(query: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to web search
            val webUri = Uri.parse("https://maps.google.com/?q=${Uri.encode(query)}")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun getDirections(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double) {
        val uri = Uri.parse("http://maps.google.com/maps?saddr=$fromLat,$fromLng&daddr=$toLat,$toLng")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // Calendar Integration
    fun createCalendarEvent(
        title: String,
        description: String? = null,
        startTime: Long,
        endTime: Long,
        location: String? = null
    ) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun viewCalendar(date: Long? = null) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = CalendarContract.CONTENT_URI
            if (date != null) {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // Contacts Integration
    fun openContact(contactId: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun searchContacts(query: String) {
        val intent = Intent(Intent.ACTION_SEARCH).apply {
            setPackage("com.android.contacts")
            putExtra("query", query)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to contacts app
            val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                data = ContactsContract.Contacts.CONTENT_URI
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallbackIntent)
        }
    }

    fun addContact(name: String, phone: String? = null, email: String? = null) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = ContactsContract.Contacts.CONTENT_URI
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            phone?.let { putExtra(ContactsContract.Intents.Insert.PHONE, it) }
            email?.let { putExtra(ContactsContract.Intents.Insert.EMAIL, it) }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // Phone Integration
    fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (hasPermission(android.Manifest.permission.CALL_PHONE) &&
            intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to dialer
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(dialIntent)
        }
    }

    // SMS Integration
    fun sendSMS(phoneNumber: String, message: String) {
        if (hasPermission(android.Manifest.permission.SEND_SMS)) {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            } catch (e: Exception) {
                // Fallback to SMS app
                openSMSApp(phoneNumber, message)
            }
        } else {
            openSMSApp(phoneNumber, message)
        }
    }

    private fun openSMSApp(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // Email Integration
    fun sendEmail(to: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$to")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // Web Browser Integration
    fun openWebsite(url: String) {
        val uri = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Uri.parse("https://$url")
        } else {
            Uri.parse(url)
        }
        
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // Settings Integration
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // Utility Methods
    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // Common app packages
    companion object {
        const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        const val TELEGRAM_PACKAGE = "org.telegram.messenger"
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val GMAIL_PACKAGE = "com.google.android.gm"
    }
}
