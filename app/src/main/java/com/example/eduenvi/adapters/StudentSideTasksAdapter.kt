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
import com.example.eduenvi.models.Task

class StudentSideTasksAdapter (private val context: Activity, private val list: List<Task>) :
    ArrayAdapter<Task>(context, R.layout.grid_student_task_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_student_task_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val groupIcon = view.findViewById<ImageView>(R.id.groupIcon)

        val task = list[position]
        name.text = task.name

        if (task.isGroup == true){
            groupIcon.visibility = View.VISIBLE
        }else if (task.isSolo == true){
            groupIcon.visibility = View.GONE
        }
        /*if (task.isTerminated == true) {

        }*/

        Constants.imageManager.setImage(task.imageId, context, image)

        view?.setOnClickListener {
            Constants.Task = task
            val intent = Intent(context, Constants.TaskTypeActivity[task.taskTypeId])
            intent.putExtra("TASK_ID", task.id)
            context.startActivity(intent)
        }
        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}