package com.example.weatherapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.view.setMargins
import com.example.weatherapp.R
import com.example.weatherapp.Utils.addZero
import com.example.weatherapp.Utils.getDate
import com.example.weatherapp.Utils.getDescription
import com.example.weatherapp.Utils.getImage
import com.example.weatherapp.Utils.truncateDecimal
import com.example.weatherapp.databinding.ActivityViewCityBinding
import org.json.JSONObject
import kotlin.properties.Delegates


class ViewCity : AppCompatActivity() {
    private lateinit var binding: ActivityViewCityBinding
    private lateinit var favourites: SharedPreferences
    private lateinit var favouritesEdit: SharedPreferences.Editor
    private var favState by Delegates.notNull<Boolean>()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WeatherApp)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_view_city)
        favState = false
        binding = ActivityViewCityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarViewCity)
        favourites = getSharedPreferences("WeatherApp", MODE_PRIVATE)
        favouritesEdit = favourites.edit()
        val jsonObj = JSONObject(intent.getStringExtra("json"))
        val city = jsonObj.getString("city")
        val state = jsonObj.getString("state")
        displayData(jsonObj)
        val button: ImageButton = binding.imageButtonViewCity
        if (jsonObj.getBoolean("currentLoc")) { ///***********
            binding.imageButtonViewCity.visibility = View.GONE ///***********
        } else { ///***********
            binding.imageButtonViewCity.visibility = View.VISIBLE
            if (favourites.contains(city)) {
                favState = true
                button.setImageResource(R.drawable.map_marker_minus)
            } else {
                favState = false
                button.setImageResource(R.drawable.map_marker_plus)
            }
            button.setOnClickListener {
                if (!favState) {
                    if (!favourites.contains(city)) {
                        favouritesEdit.putString(
                            jsonObj.getString("city"),
                            (favourites.all.count() + 1).toString() + "#" + jsonObj.toString()
                        )
                        favouritesEdit.apply()
                    }
                    button.setImageResource(R.drawable.map_marker_minus)
                    Toast.makeText(
                        this, "$city, $state was added to favourites",
                        Toast.LENGTH_LONG
                    ).show()
                    favState = true
                } else {
                    if (favourites.contains(city)) {
                        favouritesEdit.remove(city)
                        favouritesEdit.apply()
                    }
                    button.setImageResource(R.drawable.map_marker_plus)
                    Toast.makeText(
                        this, "$city, $state was removed from favourites",
                        Toast.LENGTH_LONG
                    ).show()
                    favState = false
                }
            }
        }


    }

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables", "LongLogTag", "SetTextI18n")
    private fun displayData(jsonFile: JSONObject) {
        //Log.d("From displayData TopLevelFragment:", activity?.baseContext.toString() + requireContext().toString())
        Log.d("json From displayData TopLevelFragment:", jsonFile.toString())
        val linearLayout: LinearLayout? = binding.linearViewCity
        val cardOne: CardView? = binding.cardOneViewCity
        val text: TextView = binding.cardPlaceViewCity
        text.text = jsonFile.getString("city") + ", " + jsonFile.getString("state")
        binding.toolbarTextViewCity.text =
            jsonFile.getString("city") + ", " + jsonFile.getString("state")
        cardOne?.setOnClickListener {
            Log.d("Click caught", "")
            val intent = Intent(this, Tabs::class.java)
            intent.putExtra("json", jsonFile.toString())
            startActivity(intent)
        }

        binding.backButtonViewCity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("from", "ViewCityActivity")
            intent.putExtra("favState", favState)
            intent.putExtra("json", jsonFile.toString())
            startActivity(intent)
        }
        val today = jsonFile.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
            .getJSONArray("intervals").getJSONObject(0).getJSONObject("values")
        val img: ImageView = binding.imgViewCity
        img.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                getImage(today.getString("weatherCode"))
            )
        )

        binding.temperatureViewCity.text =
            truncateDecimal(today.getDouble("temperatureApparent")) + "°F"
        binding.descriptionViewCity.text = getDescription(today.getString("weatherCode"))
        binding.humidityViewCity.text = truncateDecimal(today.getDouble("humidity")) + "%"
        binding.pressureViewCity.text = addZero(today.getDouble("pressureSeaLevel")) + "inHg"
        binding.windSpeedViewCity.text = addZero(today.getDouble("windSpeed")) + "mph"
        binding.visibilityViewCity.text = addZero(today.getDouble("visibility")) + "mi"


        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(15)
        params.leftMargin = 20
        params.rightMargin = 20

        val tableParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        tableParams.topMargin = 20
        tableParams.bottomMargin = 10

        val textParams = TableRow.LayoutParams(
            TableLayout.LayoutParams.WRAP_CONTENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.topMargin = dpToPx(14).toInt()
        textParams.bottomMargin = dpToPx(14).toInt()


        val table = TableLayout(this.baseContext)
        table.layoutParams = tableParams
        table.isStretchAllColumns = true

        val card = CardView(this.baseContext)
        card.layoutParams = params
        //card.minimumHeight = (dpToPx(80).toInt())
        //card.setPadding(0, dpToPx(50).toInt(),0, dpToPx(50).toInt())
        card.setCardBackgroundColor(resources.getColor(R.color.darkGray))
        card.cardElevation = dpToPx(5)
        card.maxCardElevation = dpToPx(5)
        card.preventCornerOverlap = true
        card.useCompatPadding = false
        card.radius = dpToPx(10)
        for (i in 1..7) {


            val tableRow = TableRow(this.baseContext)
            tableRow.layoutParams = textParams
            //tableRow.orientation = LinearLayout.HORIZONTAL
            //tableRow.setBackgroundColor(resources.getColor(R.color.darkGray))

            val day = jsonFile.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                .getJSONArray("intervals").getJSONObject(i).getJSONObject("values")

            val weatherDay = TextView(this.baseContext)
            weatherDay.text = getDate(
                jsonFile.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                    .getJSONArray("intervals").getJSONObject(i).getString("startTime")
            )
            //weatherDay.text ="2021-12-06"
            weatherDay.textSize = dpToPx(6)
            weatherDay.setTextColor(resources.getColor(R.color.white))
            weatherDay.gravity = Gravity.CENTER
            weatherDay.layoutParams = textParams
            tableRow.addView(weatherDay)

            val image = ImageView(this.baseContext)
            //image.setImageDrawable(getDrawable(resources.getIdentifier(codeDict[today.getString("weatherCode")],"drawable",".com.example.weatherapp")))
            //image.setImageDrawable(getDrawable(this.requireContext(),R.drawable.ic_clear_day))
            image.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    getImage(day.getString("weatherCode"))
                )
            )
            tableRow.addView(image)

            val tempMin = TextView(this.baseContext)
            tempMin.text = truncateDecimal(day.getDouble("temperatureMin"))
            tempMin.setTextColor(resources.getColor(R.color.white))
            tempMin.typeface = Typeface.DEFAULT_BOLD
            tempMin.textSize = dpToPx(6)
            tempMin.layoutParams = textParams
            weatherDay.gravity = Gravity.CENTER
            tableRow.addView(tempMin)

            val tempMax = TextView(this.baseContext)
            tempMax.text = truncateDecimal(day.getDouble("temperatureMax"))
            tempMax.setTextColor(resources.getColor(R.color.white))
            tempMax.typeface = Typeface.DEFAULT_BOLD
            tempMax.textSize = dpToPx(6)
            tempMax.layoutParams = textParams
            //tempMax.setPadding(0,10,0,10)
            weatherDay.gravity = Gravity.CENTER
            tableRow.addView(tempMax)

            table.addView(tableRow)
            if (table.parent != null) {
                (table.parent as ViewGroup).removeView(table) // <- fix
            }
            //card.addView(tableRow)
            card.addView(table)
            if (card.parent != null) {
                (card.parent as ViewGroup).removeView(card) // <- fix
            }
            linearLayout?.addView(card)
        }

    }

    private fun dpToPx(dp: Int): Float {
        return (dp * resources.displayMetrics.density)
    }
}