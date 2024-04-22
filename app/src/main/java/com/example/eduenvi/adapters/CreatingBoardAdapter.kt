package com.example.eduenvi.adapters

import android.app.Activity
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
import com.example.eduenvi.models.Board
import com.example.eduenvi.models.Tile

class CreatingBoardAdapter(private val context: Activity, private val list: List<Tile>, private val board: Board) :
    ArrayAdapter<Tile>(context, R.layout.board_item, list) {

    private var changingStart = false
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.board_item, null)

        val image = view.findViewById<ImageView>(R.id.image)
        if (position == board.startIndex){
            setStartImage(image)
        }
        else if (list[position].isWall) {
            setWallImage(image)
        } else {
            setFreeImage(image)
        }

        //Constants.imageManager.setImage(data, context, image)

        view?.setOnClickListener {
            if (changingStart){
                if (position != board.startIndex){
                    val viewModel: MyViewModel = ViewModelProvider(context as TaskType1CreatingActivity)[MyViewModel::class.java]
                    viewModel.setStartingPosition(position)
                    changingStart = false
                }
            }
            else if (position != board.startIndex) {
                list[position].isWall = !list[position].isWall
                if (list[position].isWall) {
                    setWallImage(image)
                } else {
                    setFreeImage(image)
                }
            }else{
                changingStart = true
            }
        }
        return view
    }

    private fun setStartImage(image: ImageView){
        if (board.startImageId == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.startImageIndex])
        } else{
            Constants.imageManager.setImage(board.startImageId, context, image)
        }
    }

    private fun setWallImage(image: ImageView){
        if (board.wallImageId != null){
            Constants.imageManager.setImage(board.wallImageId, context, image)
        } else if (Constants.ImageGridImages[Constants.wallImageIndex] == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.wallImageIndex])
        } else{
            Constants.imageManager.setImage(Constants.ImageGridImages[Constants.wallImageIndex]!!.url, context, image)
        }
    }

    private fun setFreeImage(image: ImageView){
        if (board.freeImageId != null){
            Constants.imageManager.setImage(board.freeImageId, context, image)
        } else if (Constants.ImageGridImages[Constants.freeImageIndex] == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.freeImageIndex])
        } else{
            Constants.imageManager.setImage(Constants.ImageGridImages[Constants.freeImageIndex]!!.url, context, image)
        }
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }
}