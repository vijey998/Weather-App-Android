package com.example.weatherapp.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.cardview.widget.CardView
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.Utils.addZero
import com.example.weatherapp.Utils.getDate
import com.example.weatherapp.Utils.getDescription
import com.example.weatherapp.Utils.getImage
import com.example.weatherapp.Utils.truncateDecimal
import com.example.weatherapp.activities.MainActivity
import com.example.weatherapp.activities.Tabs
import com.example.weatherapp.databinding.FragmentTopLevelBinding
import org.json.JSONObject


class TopLevelFragment : Fragment() {
    private lateinit var binding: FragmentTopLevelBinding
    private val ARG_SECTION_NUMBER = "fragment"
    private lateinit var favourites: SharedPreferences
    private lateinit var favouritesEdit: SharedPreferences.Editor
    private var sectionNumber = 0
    fun newInstance(sectionNumber: Int?, json: String): Fragment? {
        val fragment = TopLevelFragment()
        val args = Bundle()
        args.putInt(ARG_SECTION_NUMBER, sectionNumber!!)
        args.putString("json", json)
        fragment.arguments = args
        return fragment
    }

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentTopLevelBinding.inflate(layoutInflater)
        val view = binding.root
        favourites = activity?.baseContext?.getSharedPreferences("WeatherApp", MODE_PRIVATE)!!
        favouritesEdit = favourites.edit()

    }

    @SuppressLint("LongLogTag", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopLevelBinding.inflate(layoutInflater)
        var view: View = inflater.inflate(R.layout.fragment_top_level, container, false)
        if (arguments != null) {
            val json = arguments?.getString("json")
            if (json != null && json != "") {
                Log.d("From TopLevelFragment onCreateView:", json)
                val jsonObj = JSONObject(json)
                displayData(jsonObj, view)
            }
        }
        return view
    }

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables", "LongLogTag", "SetTextI18n")
    private fun displayData(jsonFile: JSONObject, view: View): View {
        Log.d(
            "From displayData TopLevelFragment:",
            activity?.baseContext.toString() + requireContext().toString()
        )
        Log.d("json From displayData TopLevelFragment:", jsonFile.toString())
        val linearLayout: LinearLayout? = view.findViewById(R.id.linearTopLevelFragment)
        val cardOne: CardView? = view.findViewById(R.id.card_one)
        val today = jsonFile.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
            .getJSONArray("intervals").getJSONObject(0).getJSONObject("values")
        val text: TextView = view.findViewById(R.id.cardPlace)
        val img: ImageView = view.findViewById(R.id.img)
        val city = jsonFile.getString("city") ///***********
        val state = jsonFile.getString("state") ///***********
        text.text = "$city, $state" ///***********
        img.setImageDrawable(
            getDrawable(
                this.requireContext(),
                getImage(today.getString("weatherCode"))
            )
        )
        (view.findViewById(R.id.temperature) as TextView).text =
            truncateDecimal(today.getDouble("temperatureApparent")) + "°F"
        (view.findViewById(R.id.description) as TextView).text =
            getDescription(today.getString("weatherCode"))
        (view.findViewById(R.id.humidity) as TextView).text =
            truncateDecimal(today.getDouble("humidity")) + "%"
        (view.findViewById(R.id.pressure) as TextView).text =
            addZero(today.getDouble("pressureSeaLevel")) + "inHg"
        (view.findViewById(R.id.windSpeed) as TextView).text =
            addZero(today.getDouble("windSpeed")) + "mph"
        (view.findViewById(R.id.visibility) as TextView).text =
            addZero(today.getDouble("visibility")) + "mi"
        val button: ImageButton = view.findViewById(R.id.imageButton)
        val currentLoc = jsonFile.getBoolean("currentLoc")
        if (!currentLoc) {
            button.visibility = View.VISIBLE
            button.setImageResource(R.drawable.map_marker_minus)
            button.setOnClickListener {
                val intent = Intent(activity?.baseContext, MainActivity::class.java)
                intent.putExtra("from", "TopLevelFragment")
                intent.putExtra("favState", false)
                intent.putExtra("json", jsonFile.toString())
                startActivity(intent)
                Toast.makeText(
                    activity?.baseContext, "$city, $state was removed from favourites",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            button.visibility = View.GONE


        }
        cardOne?.setOnClickListener {
            Log.d("Click caught", "")
            val intent = Intent(activity?.baseContext, Tabs::class.java)
            intent.putExtra("json", jsonFile.toString())
            activity?.startActivity(intent)
        }

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

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(15)
        params.leftMargin = 20
        params.rightMargin = 20

        val table = TableLayout(activity?.baseContext)
        table.layoutParams = tableParams
        table.isStretchAllColumns = true
        val card = activity?.let { CardView(it.baseContext) }
        if (card != null) {
            card.layoutParams = params
            card.setCardBackgroundColor(resources.getColor(R.color.darkGray))
            card.cardElevation = dpToPx(5)
            card.maxCardElevation = dpToPx(5)
            card.preventCornerOverlap = true
            card.useCompatPadding = false
            card.radius = dpToPx(10)
        }
        for (i in 1..7) {
            val tableRow = TableRow(activity?.baseContext)
            tableRow.layoutParams = textParams
            val day = jsonFile.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                .getJSONArray("intervals").getJSONObject(i).getJSONObject("values")
            val weatherDay = TextView(activity?.baseContext)
            weatherDay.text = getDate(
                jsonFile.getJSONObject("data").getJSONArray("timelines").getJSONObject(0)
                    .getJSONArray("intervals").getJSONObject(i).getString("startTime")
            )
            weatherDay.textSize = dpToPx(6)
            weatherDay.setTextColor(resources.getColor(R.color.white))
            weatherDay.gravity = Gravity.CENTER
            weatherDay.layoutParams = textParams
            tableRow.addView(weatherDay)

            val image = ImageView(activity?.baseContext)
            image.setImageDrawable(
                getDrawable(
                    this.requireContext(),
                    getImage(day.getString("weatherCode"))
                )
            )
            tableRow.addView(image)

            val tempMax = TextView(activity?.baseContext)
            tempMax.text = truncateDecimal(day.getDouble("temperatureMax"))
            tempMax.setTextColor(resources.getColor(R.color.white))
            tempMax.typeface = Typeface.DEFAULT_BOLD
            tempMax.textSize = dpToPx(6)
            tempMax.layoutParams = textParams
            weatherDay.gravity = Gravity.CENTER
            tableRow.addView(tempMax)
            table.addView(tableRow)

            val tempMin = TextView(activity?.baseContext)
            tempMin.text = truncateDecimal(day.getDouble("temperatureMin"))
            tempMin.setTextColor(resources.getColor(R.color.white))
            tempMin.typeface = Typeface.DEFAULT_BOLD
            tempMin.textSize = dpToPx(6)
            tempMin.layoutParams = textParams
            weatherDay.gravity = Gravity.CENTER
            tableRow.addView(tempMin)

        }
        card?.addView(table)
        linearLayout?.addView(card)
        return view
    }

    private fun dpToPx(dp: Int): Float {
        return (dp * resources.displayMetrics.density)
    }

}