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
import com.example.eduenvi.models.Task
import java.text.SimpleDateFormat

class StudentTasksAdapter(private val context: Activity, private val list: List<Task>) :
    ArrayAdapter<Task>(context, R.layout.task_x_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.task_x_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val delete = view.findViewById<ImageButton>(R.id.delete)
        val deadline = view.findViewById<TextView>(R.id.deadline)
        val visibleFrom = view.findViewById<TextView>(R.id.visibleFrom)

        val task = list[position]
        val sdf = SimpleDateFormat("dd. MM. yyyy hh:mm")

        name.text = task.name
        if (task.deadline != null) deadline.text = sdf.format(task.deadline!!)
        if (task.visibleFrom != null) visibleFrom.text = sdf.format(task.visibleFrom!!)
        Constants.imageManager.setImage(task.imageId, context, image)

        delete.setOnClickListener {
            (context as StudentTasksActivity).binding.deletePanel.visibility = View.VISIBLE
            context.binding.deleteText.text = Constants.getDeleteTaskFromStudentString(task)
            Constants.Task = task
        }
        view?.setOnClickListener {
            Constants.Task = task
            val intent = Intent(context, Constants.TaskTypeSolutionActivity[task.taskTypeId])
            intent.putExtra("TASK_ID", task.id)
            context.startActivity(intent)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}