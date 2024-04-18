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
import com.example.eduenvi.R
import com.example.eduenvi.StudentTasksActivity
import com.example.eduenvi.TaskType1CreatingActivity
import com.example.eduenvi.TaskType2CreatingActivity
import com.example.eduenvi.models.Task

class StudentTasksAdapter(private val context: Activity, private val list: List<Task>) :
    ArrayAdapter<Task>(context, R.layout.grid_x_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_x_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val delete = view.findViewById<ImageButton>(R.id.delete)

        val task = list[position]
        name.text = task.name

        Constants.imageManager.setImage(task.imageId, context, image)

        delete.setOnClickListener {
            (context as StudentTasksActivity).binding.deletePanel.visibility = View.VISIBLE
            context.binding.deleteText.text = Constants.getDeleteTaskFromStudentString(task)
            Constants.Task = task
        }
        view?.setOnClickListener {
            Constants.Task = task
            if (task.taskTypeId == Constants.TaskType1Id) {
                val intent =
                    Intent((context as StudentTasksActivity), TaskType1CreatingActivity::class.java)//TODO zmen na progres ziakov?
                context.startActivity(intent)
            } else if (task.taskTypeId == Constants.TaskType2Id) {
                val intent =
                    Intent((context as StudentTasksActivity), TaskType2CreatingActivity::class.java)
                context.startActivity(intent)
            }//TODO viac
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}