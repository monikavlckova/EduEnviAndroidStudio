package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.BoardAdapter
import com.example.eduenvi.adapters.ImageGridViewAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityTaskType1CreatingBinding
import com.example.eduenvi.models.Board
import com.example.eduenvi.models.Image
import com.example.eduenvi.models.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskType1CreatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskType1CreatingBinding
    private lateinit var boardMap: MutableList<Tile>
    private lateinit var boardAdapter: BoardAdapter
    private var boardMaps = mutableListOf<Board>()
    private var currentBoardIndex = -1
    private var updating = false
    private var taskId: Int? = null
    private lateinit var imageGridViewAdapter: ImageGridViewAdapter
    private lateinit var viewModel: MyViewModel
    private val fragment = ImageGalleryFragment()
    private var images = mutableListOf<Image?>(null, null, null, null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType1CreatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }

        imageGridViewAdapter = ImageGridViewAdapter(this)
        binding.imageGridView.adapter = imageGridViewAdapter

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(this) { image ->
            Constants.ImageGridImages[viewModel.getChangingImageIndex().value!!] = image
            imageGridViewAdapter.notifyDataChanged()

            binding.fragmentLayout.visibility = View.GONE
            binding.selectImagesPanel.visibility = View.VISIBLE
        }

        taskId = intent.extras?.getInt("TASK_ID")

        binding.numOfColumnsPicker.displayedValues =
            listOf("1", "2", "3", "4", "5", "6", "7", "8").toTypedArray()
        binding.numOfRowsPicker.displayedValues =
            listOf("1", "2", "3", "4", "5", "6", "7", "8").toTypedArray()
        binding.numOfColumnsPicker.minValue = 1
        binding.numOfColumnsPicker.maxValue = 8
        binding.numOfRowsPicker.minValue = 1
        binding.numOfRowsPicker.maxValue = 8

        binding.setBoardSize.setOnClickListener {
            binding.boardSizePickerPanel.visibility = View.GONE
            binding.mainPanel.visibility = View.VISIBLE
            val rows = binding.numOfRowsPicker.value
            val columns = binding.numOfColumnsPicker.value

            boardMap =
                (0..<(rows * columns)).map { i -> Tile(0, 0, i, false) }.toMutableList()

            if (updating) {
                boardMaps[currentBoardIndex] =
                    Board(0, rows, columns, taskId!!, 0, 0, 0, 0, boardMap)
                updating = false
            } else {
                boardMaps.add(Board(0, rows, columns, 0, 0, 0, 0, 0, boardMap))
                currentBoardIndex++
            }
            setBoard(columns)
            setPrevBoardButtonVisibility()
            setNextBoardButtonVisibility()
        }

        binding.prevBoard.setOnClickListener {
            boardMaps[currentBoardIndex].tiles = boardMap
            currentBoardIndex--
            setPrevBoardButtonVisibility()
            setNextBoardButtonVisibility()
            boardMap = boardMaps[currentBoardIndex].tiles!!
            setBoard(boardMaps[currentBoardIndex].columns)
        }

        binding.nextBoard.setOnClickListener {
            boardMaps[currentBoardIndex].tiles = boardMap
            currentBoardIndex++
            setPrevBoardButtonVisibility()
            setNextBoardButtonVisibility()
            boardMap = boardMaps[currentBoardIndex].tiles!!
            setBoard(boardMaps[currentBoardIndex].columns)
        }

        binding.moreButtons.setOnClickListener {
            binding.buttonsPanel.visibility = View.VISIBLE
            binding.moreButtons.visibility = View.GONE
            binding.closeButtonPanel.visibility = View.VISIBLE
        }

        binding.closeButtonPanel.setOnClickListener {
            closeButtonPanel()
        }

        binding.setImages.setOnClickListener {
            closeButtonPanel()
            fragment.load()
            binding.selectImagesPanel.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
        }


        binding.saveImages.setOnClickListener {
            for (i in 0..3){
                val image = Constants.ImageGridImages[i]
                if (image != null) images[i] = image
            }
            binding.selectImagesPanel.visibility = View.GONE
            binding.mainPanel.visibility = View.VISIBLE
            boardAdapter.notifyDataChanged()
        }

        binding.closeSelectImagesPanel.setOnClickListener {
            binding.selectImagesPanel.visibility = View.GONE
            Constants.ImageGridImages = images
            binding.mainPanel.visibility = View.VISIBLE
        }

        binding.deleteBoard.setOnClickListener {
            if (boardMaps.size > 0) {
                closeButtonPanel()
                boardMaps.removeAt(currentBoardIndex)
                if (boardMaps.size > 0) {
                    currentBoardIndex = boardMaps.size - 1
                    boardMap = boardMaps[currentBoardIndex].tiles!!
                    setBoard(boardMaps[currentBoardIndex].columns)
                    setPrevBoardButtonVisibility()
                    setNextBoardButtonVisibility()
                } else {
                    currentBoardIndex = -1
                    boardMap = mutableListOf()
                    setBoard(0)
                }
            }
        }

        binding.resizeBoard.setOnClickListener {
            if (boardMaps.size > 0) {
                updating = true
            }
            closeButtonPanel()
            openNewBoardPanel()
        }

        binding.addAnotherMap.setOnClickListener {
            if (boardMaps.size > 0) {
                boardMaps[currentBoardIndex].tiles = boardMap
                currentBoardIndex = boardMaps.size - 1
            }
            closeButtonPanel()
            openNewBoardPanel()
        }

        binding.saveTask.setOnClickListener {
            closeButtonPanel()
            //var task = Task(0, Constants.Teacher.id, Constants.TaskType1Id, name, text, deadline, visibleFrom)

            //TODO prejst vsetky mapy, vytvorit Board pre kazdu, dat do db, potom zistit id,
            // nastavit ho jednotlivym tiles, ulozit ich do db, vyrobit tabulku TaskBoards, vytvorit
            // Task priradit mu boards, pridat do ttaskboards, ci je circle uloha
            //boardMaps.forEach( )
        }

        binding.deleteTask.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val task = ApiHelper.deleteTask(taskId!!)
                withContext(Dispatchers.Main) {
                    if (task == null) {
                    }
                }
            }
            val intent = Intent(this, ClassroomTasksActivity::class.java)
            startActivity(intent)
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            binding.selectImagesPanel.visibility = View.VISIBLE
            //supportFragmentManager.popBackStack()
        }
    }

    private fun closeButtonPanel() {
        binding.buttonsPanel.visibility = View.GONE
        binding.moreButtons.visibility = View.VISIBLE
        binding.closeButtonPanel.visibility = View.GONE
    }

    private fun setBoard(columns: Int) {
        binding.boardNumber.text = "${currentBoardIndex + 1}/${boardMaps.size}"
        binding.board.numColumns = columns
        boardAdapter = BoardAdapter(this, boardMap)
        binding.board.adapter = boardAdapter
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

    private fun openNewBoardPanel() {
        binding.boardSizePickerPanel.visibility = View.VISIBLE
        binding.mainPanel.visibility = View.GONE
    }
}