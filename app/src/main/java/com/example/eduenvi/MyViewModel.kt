package com.example.eduenvi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eduenvi.models.Image

class MyViewModel : ViewModel() {

    private var selectedImage: MutableLiveData<Image> = MutableLiveData()
    private var changingImageIndex: MutableLiveData<Int> = MutableLiveData()

    fun setData(image: Image) {
        selectedImage.value = image
    }

    fun setData(index: Int){
        changingImageIndex.value = index
    }

    fun getSelectedImage() = selectedImage
    fun getChangingImageIndex() = changingImageIndex

}