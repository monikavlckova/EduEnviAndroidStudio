package com.example.eduenvi.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.eduenvi.Constants
import com.example.eduenvi.R
import com.example.eduenvi.StudentActivity
import com.example.eduenvi.TaskType1Activity
import com.example.eduenvi.TaskType2Activity
import com.example.eduenvi.models.Task

class StudentGroupTasksAdapter (private val context: Activity, private val list: List<Task>) :
    ArrayAdapter<Task>(context, R.layout.grid_student_task_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_student_task_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)

        val task = list[position]
        name.text = task.name

        Constants.imageManager.setImage(task.imageId, context, image)

        view?.setOnClickListener {
            Constants.Task = task
            if (task.taskTypeId == Constants.TaskType1Id) {
                val intent =
                    Intent((context as StudentActivity), TaskType1Activity::class.java)
                context.startActivity(intent)
            } else if (task.taskTypeId == Constants.TaskType2Id) {
                val intent =
                    Intent((context as StudentActivity), TaskType2Activity::class.java)
                context.startActivity(intent)
            }//TODO viac
        }
        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}