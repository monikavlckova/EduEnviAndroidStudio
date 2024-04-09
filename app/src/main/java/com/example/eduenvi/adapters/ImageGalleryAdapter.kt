package com.example.eduenvi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.Constants
import com.example.eduenvi.MyViewModel
import com.example.eduenvi.R
import com.example.eduenvi.models.Image

class ImageGalleryAdapter(private val context: FragmentActivity, private val list: List<Image>) :
    ArrayAdapter<Image>(context, R.layout.grid_image_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_image_list_item, null)

        val imageView = view.findViewById<ImageView>(R.id.image)

        val image = list[position]

        Constants.imageManager.setImage(image.url, context, imageView)

        view?.setOnClickListener {
            val viewModel: MyViewModel = ViewModelProvider(context)[MyViewModel::class.java]
            viewModel.setData(image)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}