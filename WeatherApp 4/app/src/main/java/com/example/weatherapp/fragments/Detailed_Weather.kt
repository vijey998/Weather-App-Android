package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.weatherapp.CourseModel
import com.example.weatherapp.R
import com.example.weatherapp.Utils.addZero
import com.example.weatherapp.Utils.getDescription
import com.example.weatherapp.Utils.getImage
import com.example.weatherapp.Utils.truncateDecimal
import com.example.weatherapp.adapters.GridAdapter
import org.json.JSONObject

class Detailed_Weather : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_detailed__weather, container, false)
        if (arguments != null) {
            var json = arguments?.getString("json")
            if (json != null) {
                Log.d("From Detailed Weather:", json)
                val obj = JSONObject(json)
                val today = obj.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                    .getJSONArray("intervals").getJSONObject(0).getJSONObject("values")

                var coursesGV: GridView = (view.findViewById(R.id.idGVcourses) as GridView)
                val courseModelArrayList = ArrayList<CourseModel?>()

                courseModelArrayList.add(
                    CourseModel(
                        "Wind",
                        R.drawable.weather_windy,
                        addZero(today.getDouble("windSpeed")) + " mph"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Pressure",
                        R.drawable.gauge_low,
                        addZero(today.getDouble("pressureSeaLevel")) + " inHg"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Precipitation",
                        R.drawable.weather_pouring,
                        addZero(today.getDouble("precipitationProbability")) + "%"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Temperature",
                        R.drawable.thermometer,
                        truncateDecimal(today.getDouble("temperatureApparent")) + "°F"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "",
                        getImage(today.getString("weatherCode")),
                        getDescription(today.getString("weatherCode"))
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Humidity",
                        R.drawable.water_percent,
                        truncateDecimal(today.getDouble("humidity")) + "%"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Visibility",
                        R.drawable.eye_outline,
                        addZero(today.getDouble("visibility")) + " mi"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Cloud Cover",
                        R.drawable.weather_fog,
                        addZero(today.getDouble("cloudCover")) + "%"
                    )
                )
                courseModelArrayList.add(
                    CourseModel(
                        "Ozone",
                        R.drawable.earth,
                        addZero(today.getDouble("uvIndex"))
                    )
                )
                val adapter =
                    activity?.let { GridAdapter(it.baseContext, courseModelArrayList) }
                coursesGV.adapter = adapter

            }
        }
        return view
    }
}