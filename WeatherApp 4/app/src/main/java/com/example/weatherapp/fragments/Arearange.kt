package com.example.weatherapp.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.HIGradient
import com.highsoft.highcharts.common.HIStop
import com.highsoft.highcharts.common.hichartsclasses.*
import com.highsoft.highcharts.core.HIChartView
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


class Arearange : Fragment() {

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_arearange, container, false)

        super.onCreate(savedInstanceState)

        if (arguments != null) {
            val json = arguments?.getString("json")
            if (json != null) {
                Log.d("From Arearange:", json)
                val obj = JSONObject(json)
                val today = obj.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                    .getJSONArray("intervals").getJSONObject(0).getJSONObject("values")

                val chartView: HIChartView = view.findViewById(R.id.hcarea) as HIChartView
                //(view.findViewById(R.id.chartHeader) as TextView).text = "Temperature Range"
                val options = HIOptions()
                val chart = HIChart()
                chart.type = "arearange"
                chartView.theme = "brand-light"
                chart.zoomType = "x"
                options.chart = chart
                val title = HITitle()
                title.text = "Temperature variation by day"
                options.title = title
                val xaxis = HIXAxis()
                xaxis.type = "datetime"
                options.xAxis = object : ArrayList<HIXAxis?>() {
                    init {
                        add(xaxis)
                    }
                }
                val yaxis = HIYAxis()
                yaxis.title = HITitle()
                options.yAxis = object : ArrayList<HIYAxis?>() {
                    init {
                        add(yaxis)
                    }
                }
                val tooltip = HITooltip()
                tooltip.shadow = true
                tooltip.valueSuffix = "°C"
                options.tooltip = tooltip
                val legend = HILegend()
                legend.enabled = false
                options.legend = legend
                val series = HIArearange()
                series.name = "Temperatures"
                val day = obj.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                    .getJSONArray("intervals")
                var seriesData : Array<Array<Any>> = emptyArray()
                for ( i in 0..14)
                    seriesData=seriesData.plusElement(arrayOf(
                        LocalDateTime.parse(
                            day.getJSONObject(i).getString("startTime").substring(0, 19)
                        ).toEpochSecond(ZoneOffset.UTC) * 1000,
                        day.getJSONObject(i).getJSONObject("values").getDouble("temperatureMin"),
                        day.getJSONObject(i).getJSONObject("values").getDouble("temperatureMax")
                    ))
                series.data = ArrayList(listOf(*seriesData))
                series.color = HIColor.initWithLinearGradient(
                    HIGradient(0.0F, 0.0F, 0.0F, 1.0F), LinkedList<HIStop>(
                        listOf(
                            HIStop(0.0F, HIColor.initWithHexValue("FFA500")),
                            HIStop(1.0F, HIColor.initWithHexValue("C1E1EC"))
                        )
                    )
                )
                options.series = ArrayList(Arrays.asList(series))
                chartView.options = options
            }
        }
        return view
    }

}
