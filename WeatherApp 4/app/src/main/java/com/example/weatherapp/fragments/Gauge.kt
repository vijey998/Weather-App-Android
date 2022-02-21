package com.example.weatherapp.fragments

//import com.highsoft.highcharts.common.HIChartsClasses.*

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.Utils.truncateDecimal
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.*
import com.highsoft.highcharts.core.HIChartView
import com.highsoft.highcharts.core.HIFunction
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Gauge : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_gauge, container, false)
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            var json = arguments?.getString("json")
            if (json != null) {
                Log.d("From Gauge:", json)
                val obj = JSONObject(json)
                val today = obj.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                    .getJSONArray("intervals").getJSONObject(0).getJSONObject("values")

                val renderIconsString = "function renderIcons() {" +
                        "                            if(!this.series[0].icon) {" +
                        "                               this.series[0].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[0].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[0].points[0].shapeArgs.innerR -(this.series[0].points[0].shapeArgs.r - this.series[0].points[0].shapeArgs.innerR) / 2); if(!this.series[1].icon) {this.series[1].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,'M', 8, -8, 'L', 16, 0, 8, 8]).attr({'stroke': '#ffffff','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[1].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[1].points[0].shapeArgs.innerR -(this.series[1].points[0].shapeArgs.r - this.series[1].points[0].shapeArgs.innerR) / 2); if(!this.series[2].icon) {this.series[2].icon = this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[2].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[2].points[0].shapeArgs.innerR -(this.series[2].points[0].shapeArgs.r - this.series[2].points[0].shapeArgs.innerR) / 2);}"
                val chartView = (view.findViewById(R.id.hc) as HIChartView)
                chartView.theme = "brand-light"
                val options = HIOptions()
                val chart = HIChart()
                chart.type = "solidgauge"
                chart.events = HIEvents()
                chart.events.render = HIFunction(renderIconsString)
                options.chart = chart
                val title = HITitle()
                title.text = "Stat Summary"
                title.style = HICSSObject()
                title.style.fontSize = "24px"
                options.title = title
                val tooltip = HITooltip()
                tooltip.borderWidth = 0
                tooltip.backgroundColor = HIColor.initWithName("none")
                tooltip.shadow = false
                tooltip.style = HICSSObject()
                tooltip.style.fontSize = "16px"
                tooltip.pointFormat =
                    "{series.name}<br><span style=\"font-size:2em; color: {point.color}; font-weight: bold\">{point.y}%</span>"
                tooltip.positioner = HIFunction(
                    "function (labelWidth) {" +
                            "   return {" +
                            "       x: (this.chart.chartWidth - labelWidth) /2," +
                            "       y: (this.chart.plotHeight / 2) + 15" +
                            "   };" +
                            "}"
                )
                options.tooltip = tooltip
                val pane = HIPane()
                pane.startAngle = 0
                pane.endAngle = 360
                val paneBackground1 = HIBackground()
                paneBackground1.outerRadius = "112%"
                paneBackground1.innerRadius = "88%"
                paneBackground1.backgroundColor = HIColor.initWithRGBA(102, 204, 1, 0.35)
                paneBackground1.borderWidth = 0
                val paneBackground2 = HIBackground()
                paneBackground2.outerRadius = "87%"
                paneBackground2.innerRadius = "63%"
                paneBackground2.backgroundColor = HIColor.initWithRGBA(102, 178, 255, 0.35)
                paneBackground2.borderWidth = 0
                val paneBackground3 = HIBackground()
                paneBackground3.outerRadius = "62%"
                paneBackground3.innerRadius = "38%"
                paneBackground3.backgroundColor = HIColor.initWithRGBA(239, 103, 102, 0.35)
                paneBackground3.borderWidth = 0
                pane.background =
                    ArrayList(Arrays.asList(paneBackground1, paneBackground2, paneBackground3))
                options.pane = pane
                val yaxis = HIYAxis()
                yaxis.min = 0
                yaxis.max = 100
                yaxis.lineWidth = 0
                yaxis.tickPositions = ArrayList()
                options.yAxis = ArrayList(listOf(yaxis))
                val plotOptions = HIPlotOptions()
                plotOptions.solidgauge = HISolidgauge()
                val hidLabel = HIDataLabels()
                hidLabel.enabled = false
                val datalabels = ArrayList<HIDataLabels>(arrayListOf(hidLabel))
                plotOptions.solidgauge.dataLabels = datalabels
                plotOptions.solidgauge.linecap = "round"
                plotOptions.solidgauge.stickyTracking = false
                plotOptions.solidgauge.rounded = true
                options.plotOptions = plotOptions
                val solidgauge1 = HISolidgauge()
                solidgauge1.name = "Cloud Cover"
                val data1 = HIData()
                data1.color = HIColor.initWithRGB(102, 204, 1)
                data1.radius = "112%"
                data1.innerRadius = "88%"
                data1.y = truncateDecimal(today.getDouble("cloudCover")).toInt()
                solidgauge1.data = ArrayList(listOf(data1))
                val solidgauge2 = HISolidgauge()
                solidgauge2.name = "Precipitation"
                val data2 = HIData()
                data2.color = HIColor.initWithRGB(102, 178, 255)
                data2.radius = "87%"
                data2.innerRadius = "63%"
                data2.y = truncateDecimal(today.getDouble("precipitationProbability")).toInt()
                solidgauge2.data = ArrayList(listOf(data2))
                val solidgauge3 = HISolidgauge()
                solidgauge3.name = "Humidity"
                val data3 = HIData()
                data3.color = HIColor.initWithRGB(239, 103, 102)
                data3.radius = "62%"
                data3.innerRadius = "38%"
                data3.y = truncateDecimal(today.getDouble("humidity")).toInt()
                solidgauge3.data = ArrayList(listOf(data3))
                options.series = ArrayList(Arrays.asList(solidgauge1, solidgauge2, solidgauge3))
                chartView.options = options
            }
        }
        return view

    }

}
