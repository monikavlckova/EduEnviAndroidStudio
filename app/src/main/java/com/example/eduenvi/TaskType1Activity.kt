package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.BoardAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityTaskType1Binding
import com.example.eduenvi.models.Board
import com.example.eduenvi.models.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskType1Activity : AppCompatActivity() {
    //TODO pridat moznost oznacit ze nema riesenie
    lateinit var binding: ActivityTaskType1Binding
    private lateinit var boardAdapter: BoardAdapter
    private var boardMaps = mutableListOf<Board>()
    private var currentBoardIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.paths = mutableListOf()
        loadBoards()

        binding.prevBoard.setOnClickListener {
            currentBoardIndex--
            setPrevBoardButtonVisibility()
            setNextBoardButtonVisibility()
            setBoard()
        }

        binding.nextBoard.setOnClickListener {
            currentBoardIndex++
            setPrevBoardButtonVisibility()
            setNextBoardButtonVisibility()
            setBoard()
        }

        binding.save.setOnClickListener {
            //TODO zisti ci je uloha spravne(alebo aj nie, nemusi byt spatn vazba), ak ano, uloz do db, treba vytvorit tabulku, zisti, ci uz nie
            // je ulozene riesenie, ak je nestane sa nic, ak nie ej ulozi sa, po resete si moze student
            // riesit, ale nejak oznacit ako uspesne hotove
        }

        binding.reset.setOnClickListener {
            Constants.paths[boardMaps[currentBoardIndex].index] =
                mutableListOf(boardMaps[currentBoardIndex].startIndex!!)
            setBoard()
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, StudentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun loadBoards() {
        CoroutineScope(Dispatchers.IO).launch {
            val boards = ApiHelper.getTaskBoards(Constants.Task.id) as MutableList<Board>?
            if (!boards.isNullOrEmpty()) {
                for (board in boards) {
                    board.tiles = ApiHelper.getBoardTiles(board.id) as MutableList<Tile>?
                    Constants.paths.add(mutableListOf(board.startIndex!!))
                }
                boardMaps = boards
                currentBoardIndex = 0
                withContext(Dispatchers.Main) {
                    setBoard()
                }
            }
        }
    }

    private fun setBoard() {
        binding.boardNumber.text = "${currentBoardIndex + 1}/${boardMaps.size}"
        binding.board.numColumns = boardMaps[currentBoardIndex].columns
        boardAdapter = BoardAdapter(
            this,
            boardMaps[currentBoardIndex].tiles as List<Tile>,
            boardMaps[currentBoardIndex]
        )
        binding.board.adapter = boardAdapter
        setPrevBoardButtonVisibility()
        setNextBoardButtonVisibility()
    }

    private fun setPrevBoardButtonVisibility() {
        if (currentBoardIndex <= 0) {
            binding.prevBoard.visibility = View.GONE
        } else {
            binding.prevBoard.visibility = View.VISIBLE
        }
    }

    private fun setNextBoardButtonVisibility() {
        if (currentBoardIndex >= boardMaps.size - 1) {
            binding.nextBoard.visibility = View.GONE
        } else {
            binding.nextBoard.visibility = View.VISIBLE
        }
    }
}