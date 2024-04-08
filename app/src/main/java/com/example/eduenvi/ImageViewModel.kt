package com.example.eduenvi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eduenvi.models.Image

class ImageViewModel : ViewModel() {

    private var selectedImage: MutableLiveData<Image> = MutableLiveData()

    fun setData(image: Image) {
        selectedImage.value = image
    }

    fun getSelectedImage(): LiveData<Image> {
        return selectedImage
    }
}