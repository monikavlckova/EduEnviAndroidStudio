package com.example.eduenvi.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.eduenvi.ClassroomStudentsActivity
import com.example.eduenvi.ClassroomsActivity
import com.example.eduenvi.Constants
import com.example.eduenvi.R
import com.example.eduenvi.models.Classroom

class ClassroomAdapter(private val context: Activity, private val list: List<Classroom>) :
    ArrayAdapter<Classroom>(context, R.layout.classroom_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.classroom_list_item, null)

        val classroomName = view.findViewById<TextView>(R.id.classroomName)
        val image = view.findViewById<ImageView>(R.id.classroomImage)
        val edit = view.findViewById<ImageButton>(R.id.editClassroomButton)

        val classroom = list[position]
        classroomName.text = classroom.name

        Constants.imageManager.setImage(classroom.imageId, context, image)


        edit.setOnClickListener {
            Constants.Classroom = classroom
            (context as ClassroomsActivity).binding.editPanel.visibility = View.VISIBLE
        }
        view?.setOnClickListener {
            Constants.Classroom = classroom
            val intent = Intent((context as ClassroomsActivity), ClassroomStudentsActivity::class.java)
            context.startActivity(intent)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}