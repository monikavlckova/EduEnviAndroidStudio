package com.example.eduenvi

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageManager {

    fun setImage(url: String, context: Activity, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }
}