package com.example.eduenvi.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.eduenvi.Constants
import com.example.eduenvi.R
import com.example.eduenvi.models.Tile

class BoardAdapter(private val context: Activity, private val list: List<Tile>) :
    ArrayAdapter<Tile>(context, R.layout.board_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.board_item, null)

        val image = view.findViewById<ImageView>(R.id.image)
        if (list[position].isWall) {
            if (Constants.ImageGridImages[1] == null) {
                image.setImageResource(Constants.ImageGridResources[1])
            } else{
                Constants.imageManager.setImage(Constants.ImageGridImages[1]!!.url, context, image)
            }
        } else {
            if (Constants.ImageGridImages[0] == null) {
                image.setImageResource(Constants.ImageGridResources[0])
            } else{
                Constants.imageManager.setImage(Constants.ImageGridImages[0]!!.url, context, image)
            }
        }

        //Constants.imageManager.setImage(data, context, image)

        view?.setOnClickListener {
            list[position].isWall = !list[position].isWall
            if (list[position].isWall) {
                if (Constants.ImageGridImages[1] == null) {
                    image.setImageResource(Constants.ImageGridResources[1])
                } else{
                    Constants.imageManager.setImage(Constants.ImageGridImages[1]!!.url, context, image)
                }
            } else {
                if (Constants.ImageGridImages[0] == null) {
                    image.setImageResource(Constants.ImageGridResources[0])
                } else{
                    Constants.imageManager.setImage(Constants.ImageGridImages[0]!!.url, context, image)
                }
            }
        }
        return view
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }
}