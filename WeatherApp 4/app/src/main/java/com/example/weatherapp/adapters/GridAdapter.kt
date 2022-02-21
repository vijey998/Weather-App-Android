package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapp.CourseModel
import com.example.weatherapp.R

class GridAdapter(context: Context, courseModelArrayList: ArrayList<CourseModel?>?) :
    ArrayAdapter<CourseModel?>(context, 0, courseModelArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listitemView = convertView
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView =
                LayoutInflater.from(context).inflate(R.layout.grid_card_layout, parent, false)
        }
        val courseModel = getItem(position)
        val courseTV = listitemView!!.findViewById<TextView>(R.id.idTVCourse)
        val courseIV = listitemView.findViewById<ImageView>(R.id.idIVcourse)
        val courseEV = listitemView.findViewById<TextView>(R.id.json_text)
        courseTV.text = courseModel!!.course_name
        courseIV.setImageResource(courseModel.imgid)
        courseEV.text = courseModel.jsontext
        return listitemView
    }
}
