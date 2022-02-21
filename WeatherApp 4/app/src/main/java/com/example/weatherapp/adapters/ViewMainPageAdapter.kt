package com.example.weatherapp

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.weatherapp.fragments.TopLevelFragment
import org.json.JSONObject


class ViewMainPageAdapterAdapter(supportFragmentManager: FragmentManager) :
    FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var baseId = 0
    private var fragmentList = ArrayList<Fragment>()
    private var fragmentTitleList = ArrayList<String>()
    var fragmentCityList = ArrayList<String>()
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        Log.d("Position", position.toString() + getPageTitle(position).toString())
        return TopLevelFragment().newInstance(position, getPageTitle(position).toString())!!
    }

    override fun getPageTitle(position: Int): String? {
        return fragmentTitleList[position]
    }

    fun addFragment(fragment: TopLevelFragment, title: String) {

        fragmentList.add(fragment)
        fragmentTitleList.add(title)
        fragmentCityList.add(JSONObject(title).getString("city"))
        //baseId+=1
        Log.d(
            "From ViewPager Add",
            fragmentList.size.toString() + " " + fragmentTitleList.size.toString() + " " + fragment.id.toString()
        )
    }

    fun removeFragment(city: String): Int {
        val position: Int = fragmentCityList.indexOf(city)
        for (s in fragmentCityList) {
            Log.d("ViewPager City:", s)
        }
        //fragmentList.removeAt(position)
        //fragmentTitleList.removeAt(position)
        //fragmentCityList.removeAt(position)
        Log.d(
            "From ViewPager Remove",
            city + " " + fragmentList.size.toString() + " " + fragmentTitleList.size.toString() + " "
        )
        return position
    }

    fun getCity(position: Int): String {
        return fragmentCityList[position]
    }

    fun reset() {
        fragmentList = ArrayList<Fragment>()
        fragmentTitleList = ArrayList<String>()
        fragmentCityList = ArrayList<String>()
    }

    override fun getItemPosition(`object`: Any): Int {
        // refresh all fragments when data set changed
        return POSITION_NONE
    }

    override fun getItemId(position: Int): Long {
        // give an ID different from position when position has been changed
        return (baseId + position).toLong()
    }

    fun notifyChangeInPosition(n: Int) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += count + n
    }


}