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
import com.example.eduenvi.TasksActivity
import com.example.eduenvi.models.Task

class TasksAdapter(private val context: Activity, private val list: List<Task>) :
    ArrayAdapter<Task>(context, R.layout.grid_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val edit = view.findViewById<ImageButton>(R.id.edit)

        val task = list[position]

        name.text = task.name
        Constants.imageManager.setImage(task.imageId, context, image)

        edit.setOnClickListener {
            (context as TasksActivity).binding.editPanel.visibility = View.VISIBLE
            Constants.Task = task
        }
        view?.setOnClickListener {
            Constants.Task = task
            val intent = Intent(context, Constants.TaskTypeCreatingActivity[task.taskTypeId])
            //TODO zmen zoznam ziakov/skuipin, kt maju ulohu
            intent.putExtra("TASK_ID", task.id)
            context.startActivity(intent)
        }

        return view
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }
}