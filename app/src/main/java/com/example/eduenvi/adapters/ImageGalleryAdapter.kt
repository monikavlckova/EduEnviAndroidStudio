package com.example.eduenvi.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.eduenvi.Constants
import com.example.eduenvi.ImageGalleryActivity
import com.example.eduenvi.ProfileActivity
import com.example.eduenvi.R
import com.example.eduenvi.models.Image

class ImageGalleryAdapter(private val context: Activity, private val list: List<Image>) :
    ArrayAdapter<Image>(context, R.layout.grid_image_list_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_image_list_item, null)

        val imageView = view.findViewById<ImageView>(R.id.image)

        val image = list[position]

        Constants.imageManager.setImage(image.url, context, imageView)

        view?.setOnClickListener {
            Constants.Image = image
            val intent = Intent((context as ImageGalleryActivity), ProfileActivity::class.java) //TODO otvor posledny
            intent.putExtra("IMAGE_CHANGED", true)
            context.startActivity(intent)
        }

        return view
    }

    fun notifyDataChanged(){
        notifyDataSetChanged()
    }
}