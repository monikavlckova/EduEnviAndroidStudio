package com.example.eduenvi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.Constants
import com.example.eduenvi.MyViewModel
import com.example.eduenvi.R
import com.example.eduenvi.TaskType1CreatingActivity

class ImageGridViewAdapter(private val context: TaskType1CreatingActivity, list: List<String> = mutableListOf("", "", "", "")) :
    ArrayAdapter<String>(context, R.layout.board_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.board_item, null)

        val image = view.findViewById<ImageView>(R.id.image)

        if (Constants.ImageGridImages[position] == null) {
            image.setImageResource(Constants.ImageGridResources[position])
        } else{
            Constants.imageManager.setImage(Constants.ImageGridImages[position]!!.url, context, image)
        }

        view?.setOnClickListener {
            context.binding.fragmentLayout.visibility = View.VISIBLE
            context.binding.selectImagesPanel.visibility = View.GONE
            val viewModel: MyViewModel = ViewModelProvider(context)[MyViewModel::class.java]
            viewModel.setChangingImageIndex(position)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}