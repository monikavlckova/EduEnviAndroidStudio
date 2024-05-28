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
import com.example.eduenvi.Constants
import com.example.eduenvi.GroupStudentsActivity
import com.example.eduenvi.R
import com.example.eduenvi.StudentTasksActivity
import com.example.eduenvi.models.Student

class GroupStudentsAdapter (private val context: Activity, private val list: List<Student>) :
    ArrayAdapter<Student>(context, R.layout.grid_x_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_x_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val delete = view.findViewById<ImageButton>(R.id.delete)

        val student = list[position]
        name.text = student.firstName

        Constants.imageManager.setImage(student.imageId, context, image)

        delete.setOnClickListener {
            (context as GroupStudentsActivity).binding.deletePanel.visibility = View.VISIBLE
            context.binding.deleteText.text = Constants.getDeleteStudentFromGroupString(student)
            Constants.Student = student
        }
        view?.setOnClickListener {
            Constants.Student = student
            val intent = Intent((context as GroupStudentsActivity), StudentTasksActivity::class.java)//TODO zmen, nechod nikam?
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}