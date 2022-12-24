package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * get list of 7 days with format yyyy-mm-dd
 * and could be used as parameter of Retrofit query
 */
fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return formattedDateList
}

/**
 * get Today string as following format
 * yyyy-MM-dd
 */
fun getTodayString(): String {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    val today = Calendar.getInstance().time
    return dateFormat.format(today)
}

/**
 * Check target day is before or after other day
 */
fun String.isAfter(date: String): Boolean {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    val org = dateFormat.parse(this) as Date
    val target = dateFormat.parse(date) as Date
    return org.time > target.time
}