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
import com.example.eduenvi.ClassStudentsActivity
import com.example.eduenvi.Constants
import com.example.eduenvi.R
import com.example.eduenvi.StudentTasksActivity
import com.example.eduenvi.models.Student

class ClassroomStudentsAdapter (private val context: Activity, private val list: List<Student>) :
    ArrayAdapter<Student>(context, R.layout.classroom_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val edit = view.findViewById<ImageButton>(R.id.edit)

        val student = list[position]
        name.text = student.name

        edit.setOnClickListener {
            Constants.Student = student
            (context as ClassStudentsActivity).binding.editPanel.visibility = View.VISIBLE
        }
        view?.setOnClickListener {
            Constants.Student = student
            val intent = Intent((context as ClassStudentsActivity), StudentTasksActivity::class.java)
            context.startActivity(intent)
        }
        return view
    }

    fun notifyDataChenged(){
        notifyDataSetChanged()
    }
}