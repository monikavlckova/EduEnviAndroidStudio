package com.example.eduenvi

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.models.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageManager {

    fun setImage(url: String, context: Activity, imageView: ImageView) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.baseline_image_on_primary_24)
            .error(R.drawable.baseline_image_on_primary_24)

        Glide.with(context)
            .load(url)
            .apply(options)
            .into(imageView)
    }

    fun setImage(imageId: Int?, context: Activity, imageView: ImageView) {
        var url = ""
        CoroutineScope(Dispatchers.IO).launch {
            if (imageId != null) {
                val dbImage: Image? = ApiHelper.getImage(imageId)
                if (dbImage != null) url = dbImage.url
            }
            withContext(Dispatchers.Main) {
                setImage(url, context, imageView)
            }
        }
    }
}