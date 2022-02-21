package com.example.weatherapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.example.weatherapp.R
import com.example.weatherapp.ViewMainPageAdapterAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.TopLevelFragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime


//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var favourites: SharedPreferences
    private lateinit var favouritesEdit: SharedPreferences.Editor
    lateinit var requestQueue: RequestQueue
    private lateinit var binding: ActivityMainBinding
    private var adapter = ViewMainPageAdapterAdapter(supportFragmentManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WeatherApp)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setSupportActionBar(binding.toolbar)
        setContentView(view)
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocompleteFragment) as AutocompleteSupportFragment
        favourites = getSharedPreferences("WeatherApp", MODE_PRIVATE)
        favouritesEdit = favourites.edit()
        val appnetwork = BasicNetwork(HurlStack())
        val appcache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap
        requestQueue = RequestQueue(appcache, appnetwork).apply {
            start()
        }
        val button: ImageButton = binding.searchButton
        button.setOnClickListener {
            autocompleteFragment.view?.findViewById<AppCompatImageButton>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)
                ?.performClick()
        }

        val from = intent.getStringExtra("from")
        Log.d("Resumption create:", callingActivity.toString() + " " + from)
        Log.d("From Activity:", from.toString())
        val favState = intent.getBooleanExtra("favState", false)
        val data = intent.getStringExtra("json").toString()
        if (data != "" && data != "null") {
            val jsonObj = JSONObject(data)
            if (from == "ViewCityActivity") {
                if (favState) {
                    addFavourite(jsonObj)
                } else {
                    removeFavourite(jsonObj)
                }
                renderApp()
            } else if (from == "TopLevelFragment") {
                removeFavourite(jsonObj)
                renderApp()
            } else {
                renderApp()
            }
        } else {
            renderApp()
        }
    }

    private fun renderApp() {
        enable()
        initLayout("create")
        Handler().postDelayed({
            disable()
            renderViewPager()
        }, 3000)
    }

    @SuppressLint("LongLogTag", "NewApi")
    fun setupViewPager(jsonObj: JSONObject, op: String) {
        Log.d("Before Fragment Setup", jsonObj.getString("city") + "," + jsonObj.getString("state"))
        val bundle = Bundle()
        binding.mainTab.addTab(binding.mainTab.newTab())
        bundle.putString("json", jsonObj.toString())
        bundle.putInt("fragment", binding.mainTab.tabCount - 1)
        val details = TopLevelFragment()
        details.arguments = bundle
        adapter.addFragment(details, jsonObj.toString())

    }

    @SuppressLint("LongLogTag")
    fun renderViewPager() {
        binding.mainViewPager.adapter = adapter //
        binding.mainViewPager.adapter?.notifyDataSetChanged()
        binding.mainViewPager.offscreenPageLimit = 10
    }

    private fun enable() {
        binding.MainActivityLinear.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.progressText.visibility = View.VISIBLE
    }

    private fun disable() {
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
        binding.MainActivityLinear.visibility = View.VISIBLE
    }

    @SuppressLint("LongLogTag")
    private fun addFavourite(jsonObj: JSONObject) {
        Log.d("Timestamp Adding city from fav:", jsonObj.getString("city"))
        val cityHere = jsonObj.getString("city")
        if (!favourites.contains(cityHere)) {
            favouritesEdit.putString(cityHere, jsonObj.toString())
            favouritesEdit.apply()
        }
        favouritesEdit.putString(
            jsonObj.getString("city"),
            (favourites.all.count() + 1).toString() + "#" + jsonObj.toString()
        )
        favouritesEdit.apply()
        Log.d("Timestamp added city:", jsonObj.getString("city"))
        //initLayout("add")
        //setupViewPager(jsonObj, "add")
    }

    @SuppressLint("LongLogTag")
    private fun removeFavourite(jsonObj: JSONObject) {
        Log.d("Timestamp Removing city from fav:", jsonObj.getString("city"))
        val cityHere = jsonObj.getString("city")
        if (favourites.contains(cityHere)) {
            favouritesEdit.remove(jsonObj.getString("city"))
            favouritesEdit.apply()
        }
        Log.d("Timestamp removed city:", jsonObj.getString("city"))
    }


    @SuppressLint("NewApi", "LongLogTag")
    private fun initLayout(STATE: String) {
        for (key in favourites.all)
            Log.d("initBoss: ", "$STATE $key")
        val ipInfoUrl = "https://ipinfo.io?token="+getString(R.string.ipinfo_api_key)
        var currentLat: String = ""
        var currentLng: String = ""
        var currentCity: String = ""
        var currentState: String = ""
        val jsonObjectRequestCurrent = JsonObjectRequest(
            Request.Method.GET, ipInfoUrl, null,
            { response ->
                Log.d("IP info Geocoding:", response.toString())
                val obj = response.getString("loc")
                currentLat = obj.split(",")[0]
                currentLng = obj.split(",")[1]
                currentCity = response.getString("city")
                currentState = response.getString("region")
                Log.d("Timestamp Current Location fetched:", currentCity)
                if (!favourites.contains(currentCity)) {
                    fetchData(currentLat, currentLng, true, currentCity, currentState, false)
                    Handler().postDelayed({
                        for (key in favourites.all.toList()
                            .sortedBy { (_, value) -> value as String }.toMap()) {
                            Log.d("Boss: ", "$STATE $key")
                            val jsonMemory = JSONObject((key.value as String).split('#')[1])
                            val date = jsonMemory.getJSONObject("data").getJSONArray("timelines")
                                .getJSONObject(0).getJSONArray("intervals").getJSONObject(0)
                                .getString("startTime").substring(0, 10)
                            if (date != LocalDate.now()
                                    .toString() && LocalTime.now() > LocalTime.of(6, 0)
                            ) {
                                getLatLonGoogle(
                                    jsonMemory.getString("city") + "," +
                                            jsonMemory.getString("state"), true
                                )
                            } else {
                                setupViewPager(jsonMemory, "add")
                            }
                        }
                    }, 1000)
                }
            },
            { error ->
                Log.e("Error fetching lat/lng:", error.toString())
            })

        requestQueue.add(jsonObjectRequestCurrent)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.googlePlaces_api_key))
        }
        var placesClient = Places.createClient(this)

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setCountry("US")
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("Google Autocomplete:", "Place: ${place.name}, ${place.id}")
                getLatLonGoogle(place.name.toString(), false)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("Google Autocomplete:", "An error occurred: $status")

            }
        })

    }


    private fun getLatLonGoogle(place: String, favourite: Boolean) {
        val url =
            "https://maps.googleapis.com/maps/api/geocode/json?address=$place&key="+ getString(R.string.googleGeocoding_api_key)
        Log.d("Geocoding URL:", url)
        var lat: String = ""
        var lng: String = ""
        var city: String = ""
        var state: String = ""
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("Google Geocoding:", response.toString())
                val obj = response.getJSONArray("results").getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                lat = obj.getString("lat")
                lng = obj.getString("lng")
                //city = response.address_components.get(0).long_name
                city = response.getJSONArray("results").getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(0)
                    .getString("long_name")
                for (i in 0 until response.getJSONArray("results").getJSONObject(0)
                    .getJSONArray("address_components").length()) {
                    if (response.getJSONArray("results").getJSONObject(0)
                            .getJSONArray("address_components").getJSONObject(i)
                            .getJSONArray("types")
                            .getString(0) == "administrative_area_level_1"
                    ) {
                        state = response.getJSONArray("results").getJSONObject(0)
                            .getJSONArray("address_components").getJSONObject(i)
                            .getString("long_name")
                    }
                }
                fetchData(lat, lng, false, city, state, favourite)
            },
            { error ->
                Log.e("Error fetching lat/lng:", error.toString())
            })
        requestQueue.add(jsonObjectRequest)
    }

    @SuppressLint("SetTextI18n", "LongLogTag")
    fun fetchData(
        lat: String,
        lng: String,
        currentLoc: Boolean,
        city: String,
        state: String,
        favourite: Boolean
    ) {
        val url =
            "https://weather-backend-dot-weatherappvijey998.wl.r.appspot.com/getWeather?lat=${lat}&lng=${lng}"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.i("Fetched: ", response.toString())
                response.put("city", city)
                response.put("state", state)
                response.put("currentLoc", currentLoc)
                // TODO if CurrentLoc doesn't exist in SP
                if (currentLoc || favourite) {
                    Log.d("Timestamp SetupViewpager from Fetchdata:", response.getString("city"))
                    setupViewPager(response, "add")
                } else {
                    enable()
                    Handler().postDelayed({
                        val intent = Intent(this, ViewCity::class.java)
                        intent.putExtra("json", response.toString())
                        startActivity(intent)
                    }, 1000)

                }
            },
            { error ->
                Log.e("Error in weather fetch:", error.toString())
                enable()
                Toast.makeText(
                    this, "It's taking some time to load...",
                    Toast.LENGTH_LONG
                ).show()
                Handler().postDelayed({
                    disable()
                    recreate()
                }, 10000/* 5 second */)
            }
        )
        requestQueue.add(jsonObjectRequest)

    }


}

