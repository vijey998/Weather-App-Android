package com.example.weatherapp

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round


class CourseModel(
    var course_name: String, var imgid: Int, var jsontext: String
)

object Utils {
    fun Utils() {}
    fun getDescription(weatherCode: String): String {
        val descDict = mapOf(
            0 to "Unknown",
            1000 to "Clear",
            1001 to "Cloudy",
            1100 to "Mostly Clear",
            1101 to "Partly Cloudy",
            1102 to "Mostly Cloudy",
            2000 to "Fog",
            2100 to "Light Fog",
            3000 to "Light Wind",
            3001 to "Wind",
            3002 to "Strong Wind",
            4000 to "Drizzle",
            4001 to "Rain",
            4200 to "Light Rain",
            4201 to "Heavy Rain",
            5000 to "Snow",
            5001 to "Flurries",
            5100 to "Light Snow",
            5101 to "Heavy Snow",
            6000 to "Freezing Drizzle",
            6001 to "Freezing Rain",
            6200 to "Light Freezing Rain",
            6201 to "Heavy Freezing Rain",
            7000 to "Ice Pellets",
            7101 to "Heavy Ice Pellets",
            7102 to "Light Ice Pellets",
            8000 to "Thunderstorm"
        )
        return descDict[weatherCode.toInt()]!!
    }

    fun getImage(weatherCode: String): Int {
        val codeDict = mapOf(
            "1000" to R.drawable.ic_clear_day,
            "1100" to R.drawable.ic_mostly_clear_day,
            "1101" to R.drawable.ic_partly_cloudy_day,
            "1102" to R.drawable.ic_mostly_cloudy,
            "1001" to R.drawable.ic_cloudy,
            "2000" to R.drawable.ic_fog,
            "2100" to R.drawable.ic_fog_light,
            "8000" to R.drawable.ic_tstorm,
            "5001" to R.drawable.ic_flurries,
            "5100" to R.drawable.ic_snow_light,
            "5000" to R.drawable.ic_snow,
            "5101" to R.drawable.ic_snow_heavy,
            "7102" to R.drawable.ic_ice_pellets_light,
            "7000" to R.drawable.ic_ice_pellets,
            "7101" to R.drawable.ic_ice_pellets_heavy,
            "4000" to R.drawable.ic_drizzle,
            "6000" to R.drawable.ic_freezing_drizzle,
            "6200" to R.drawable.ic_freezing_rain_light,
            "6001" to R.drawable.ic_freezing_rain,
            "6201" to R.drawable.ic_freezing_rain_heavy,
            "4200" to R.drawable.ic_rain_light,
            "4001" to R.drawable.ic_rain,
            "4201" to R.drawable.ic_rain_heavy,
            "3000" to R.drawable.weather_windy,
            "3001" to R.drawable.weather_windy,
            "3002" to R.drawable.weatherwindyvariant,
        )
        return codeDict[weatherCode]!!
    }

    fun string2float(num: Double): Double {
        val x = 0
        val decimal: Double =
            BigDecimal(num.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        return try {
            decimal
        } catch (e: NumberFormatException) {
            x.toDouble()
        }

    }

    fun addZero(num: Double): String {
        var x = string2float(num).toString().split('.')
        if (x[1].length == 1)
            return x[0] + "." + x[1] + "0"
        return x[0] + "." + x[1]
    }

    fun truncateDecimal(num: Double): String {
        return round(string2float(num)).toInt().toString()

    }

    fun getDate(time: String): String {
        return time.substring(0, 10)
    }

}