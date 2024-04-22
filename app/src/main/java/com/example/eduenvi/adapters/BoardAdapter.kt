package com.example.eduenvi.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.eduenvi.Constants
import com.example.eduenvi.R
import com.example.eduenvi.models.Board
import com.example.eduenvi.models.Tile
import kotlin.math.abs

class BoardAdapter(
    private val context: Activity,
    private val list: List<Tile>,
    private val board: Board
) :
    ArrayAdapter<Tile>(context, R.layout.board_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.board_item, null)

        val image = view.findViewById<ImageView>(R.id.image)
        if (position == board.startIndex) {
            setStartImage(image)
        } else if (list[position].isWall) {
            setWallImage(image)
        } else if (Constants.paths[board.index].contains(position)) {
            setItemImage(image)
        } else {
            setFreeImage(image)
        }

        view?.setOnClickListener {
            if (!list[position].isWall && position != board.startIndex
                && !Constants.paths[board.index].contains(position)
                && isNeighbour(Constants.paths[board.index].last(), position)
            ) {
                Constants.paths[board.index].add(position)
                setItemImage(image)
            }
        }
        return view
    }

    private fun setStartImage(image: ImageView) {
        if (board.startImageId == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.startImageIndex])
        } else {
            Constants.imageManager.setImage(board.startImageId, context, image)
        }
    }

    private fun setWallImage(image: ImageView) {
        if (board.wallImageId == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.wallImageIndex])
        } else {
            Constants.imageManager.setImage(board.wallImageId, context, image)
        }
    }

    private fun setFreeImage(image: ImageView) {
        if (board.freeImageId == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.freeImageIndex])
        } else {
            Constants.imageManager.setImage(board.freeImageId, context, image)
        }
    }

    private fun setItemImage(image: ImageView) {
        if (board.itemImageId == null) {
            image.setImageResource(Constants.ImageGridResources[Constants.itemImageIndex])
        } else {
            Constants.imageManager.setImage(board.itemImageId, context, image)
        }
    }

    private fun isNeighbour(i: Int, j: Int): Boolean {
        val rI = i / board.columns
        val cI = i % board.columns
        val rJ = j / board.columns
        val cJ = j % board.columns
        return ((rI == rJ) && abs(cI - cJ) == 1) || ((cI == cJ) && abs(rI - rJ) == 1)
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }
}