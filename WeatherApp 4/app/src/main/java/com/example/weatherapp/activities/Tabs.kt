package com.example.weatherapp.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.Utils.truncateDecimal
import com.example.weatherapp.adapters.ViewPagerAdapter
import com.example.weatherapp.databinding.ActivityTabsBinding
import com.example.weatherapp.fragments.Arearange
import com.example.weatherapp.fragments.Detailed_Weather
import com.example.weatherapp.fragments.Gauge
import org.json.JSONObject


class Tabs : AppCompatActivity() {
    private lateinit var binding: ActivityTabsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WeatherApp)
        super.onCreate(savedInstanceState)
        binding = ActivityTabsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //setContentView(R.layout.activity_tabs)
        val jsonObj = JSONObject(intent.getStringExtra("json"))
        Log.d("Inside Tab Activity", jsonObj.toString())
        setTabLayout(jsonObj)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun setTabLayout(jsonObj: JSONObject) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        binding.viewPager.offscreenPageLimit = 0
        val bundle = Bundle()
        bundle.putString("json", jsonObj.toString())
        val details = Detailed_Weather()
        val arearange = Arearange()
        val gauge = Gauge()
        details.arguments = bundle
        arearange.arguments = bundle
        gauge.arguments = bundle
        /*
        adapter.addFragment(details,"TODAY")
        adapter.addFragment(arearange,"WEEKLY")
        adapter.addFragment(gauge,"WEATHER DATA")
        binding.tabs.getTabAt(0)!!.icon = getDrawable(R.drawable.calendar_today)
        binding.tabs.getTabAt(1)!!.icon = getDrawable(R.drawable.trending_up)
        binding.tabs.getTabAt(2)!!.icon = getDrawable(R.drawable.thermometer_low)
        binding.toolbarTextTabs.text= jsonObj.getString("city")+", "+jsonObj.getString("state")
        binding.tabs.setTabTextColors(resources.getColor(R.color.white),resources.getColor(R.color.white))
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter = adapter*/


        adapter.addFragment(details, "TODAY")
        adapter.addFragment(arearange, "WEEKLY")
        adapter.addFragment(gauge, "WEATHER DATA")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.getTabAt(0)!!.icon = getDrawable(R.drawable.calendar_today)
        binding.tabs.getTabAt(1)!!.icon = getDrawable(R.drawable.trending_up)
        binding.tabs.getTabAt(2)!!.icon = getDrawable(R.drawable.thermometer_low)
        binding.toolbarTextTabs.text = jsonObj.getString("city") + ", " + jsonObj.getString("state")
        binding.tabs.setTabTextColors(
            resources.getColor(R.color.white),
            resources.getColor(R.color.white)
        )


        val today = jsonObj.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
            .getJSONArray("intervals").getJSONObject(0).getJSONObject("values")
        binding.twitterTabs.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://twitter.com/intent/tweet?text=Check%20out%20" +
                            jsonObj.getString("city") +
                            ",%20" + jsonObj.getString("state") + "%20weather!%20It%20is%20" +
                            truncateDecimal(today.getDouble("temperatureApparent")) + "°F!" +
                            "&hashtags=CSCI571WeatherSearch"
                )
            )
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            browserIntent.setPackage("com.android.chrome")
            try {
                startActivity(browserIntent)
            } catch (ex: ActivityNotFoundException) {
                // Chrome browser presumably not installed so allow user to choose instead
                browserIntent.setPackage(null)
                startActivity(browserIntent)
            }
        }
        binding.backButtonTabs.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}