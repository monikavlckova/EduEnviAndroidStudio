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
import com.example.eduenvi.ClassroomGroupsActivity
import com.example.eduenvi.Constants
import com.example.eduenvi.GroupTasksActivity
import com.example.eduenvi.R
import com.example.eduenvi.models.Group

class ClassroomGroupsAdapter (private val context: Activity, private val list: List<Group>) :
    ArrayAdapter<Group>(context, R.layout.grid_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
        val edit = view.findViewById<ImageButton>(R.id.edit)

        val group = list[position]
        name.text = group.name

        Constants.imageManager.setImage(group.imageId, context, image)

        edit.setOnClickListener {
            Constants.Group = group
            (context as ClassroomGroupsActivity).binding.editPanel.visibility = View.VISIBLE
        }
        view?.setOnClickListener {
            Constants.Group = group
            val intent = Intent((context as ClassroomGroupsActivity), GroupTasksActivity::class.java)
            context.startActivity(intent)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}