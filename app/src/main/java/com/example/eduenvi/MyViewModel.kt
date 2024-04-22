package com.example.eduenvi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eduenvi.models.Image

class MyViewModel : ViewModel() {

    private var selectedImage: MutableLiveData<Image> = MutableLiveData()
    private var changingImageIndex: MutableLiveData<Int> = MutableLiveData()
    private var startingPosition: MutableLiveData<Int> = MutableLiveData()

    fun setData(image: Image) {
        selectedImage.value = image
    }

    fun setChangingImageIndex(index: Int){
        changingImageIndex.value = index
    }

    fun setStartingPosition(position: Int){
        startingPosition.value = position
    }

    fun getSelectedImage() = selectedImage
    fun getChangingImageIndex() = changingImageIndex

    fun getStartingPosition() = startingPosition

}