package com.example.jadwalkuliah.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val localeId = Locale("id", "ID")
    private val deadlineFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", localeId)

    fun formatDeadline(timestamp: Long?): String {
        if (timestamp == null) return "Tanpa Deadline"
        return deadlineFormatter.format(Date(timestamp))
    }

    fun getCountdown(deadline: Long?): String {
        if (deadline == null) return ""
        val now = System.currentTimeMillis()
        val diff = deadline - now
        val diffAbs = Math.abs(diff)

        val days = TimeUnit.MILLISECONDS.toDays(diffAbs)
        val hours = TimeUnit.MILLISECONDS.toHours(diffAbs) % 24

        return if (diff > 0) {
            when {
                days > 0 -> "$days hari lagi"
                hours > 0 -> "$hours jam lagi"
                else -> "Deadline hari ini"
            }
        } else {
            if (days == 0L) {
                "Terlambat beberapa saat"
            } else {
                "Terlambat $days hari"
            }
        }
    }

    fun isUrgent(deadline: Long?): Boolean {
        if (deadline == null) return false
        val now = System.currentTimeMillis()
        val diff = deadline - now
        return diff > 0 && diff < TimeUnit.DAYS.toMillis(1)
    }

    fun isOverdue(deadline: Long?): Boolean {
        if (deadline == null) return false
        return System.currentTimeMillis() > deadline
    }

    fun getFileName(context: Context, uriString: String): String {
        val uri = Uri.parse(uriString)
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = cursor.getString(index)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "File"
    }
}
