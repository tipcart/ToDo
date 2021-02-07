package com.wywrot.todo.utils

import java.text.SimpleDateFormat
import java.util.*

object Utils {

    private const val stringFormat = "MM/dd/yyyy"

    fun getDateTime(millis: Long): String? {
        return try {
            val sdf = SimpleDateFormat(stringFormat, Locale.getDefault())
            val date = Date(millis)
            sdf.format(date)
        } catch (e: Exception) {
            e.toString()
        }
    }
}