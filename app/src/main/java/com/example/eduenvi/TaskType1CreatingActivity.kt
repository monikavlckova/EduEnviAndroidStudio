package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.BoardCreatingAdapter
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
//TODO ked zahodim plochu, a je len jedna, tak otvorit boardSizePickerPanel

//TODO ci je circle uloha - spravit dalsi typ? dat dext do board nie do tasku? alebo aj aj
class TaskType1CreatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskType1CreatingBinding
    private lateinit var boardMap: MutableList<Tile>
    private lateinit var boardAdapter: BoardCreatingAdapter
    private var boardMaps = mutableListOf<Board>()
    private var currentBoardIndex = -1
    private var updating = false
    private var taskId: Int? = null
    private lateinit var imageGridViewAdapter: ImageGridViewAdapter
    private lateinit var viewModel: MyViewModel
    private val fragment = ImageGalleryFragment()
    private var images = mutableListOf<Image?>(null, null, null, null)
    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType1CreatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.extras?.getInt("TASK_ID")
        loadBoardsIfExist()

        loadGalleryFragment(savedInstanceState)

        imageGridViewAdapter = ImageGridViewAdapter(myContext)
        binding.imageGridView.adapter = imageGridViewAdapter

        loadViewModel()

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

            val board = Board(0, rows, columns, 0, taskId!!, 0, null, null, null, null, boardMap)
            if (updating) {
                boardMaps[currentBoardIndex] = board
                updating = false
            } else {
                boardMaps.add(board)
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
            for (i in 0..3) {
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
                    binding.mainPanel.visibility = View.GONE
                    binding.boardSizePickerPanel.visibility = View.VISIBLE
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

        binding.addAnotherBoard.setOnClickListener {
            if (boardMaps.size > 0) {
                boardMaps[currentBoardIndex].tiles = boardMap
                currentBoardIndex = boardMaps.size - 1
            }
            closeButtonPanel()
            openNewBoardPanel()
        }

        binding.saveTask.setOnClickListener {
            closeButtonPanel()
            CoroutineScope(Dispatchers.IO).launch {
                deleteTaskBoards()
                for ((i, boardMap) in boardMaps.withIndex()) {
                    val tiles = boardMap.tiles
                    boardMap.id = 0
                    boardMap.tiles = null
                    boardMap.index = i
                    boardMap.freeImageId = images[Constants.freeImageIndex]?.id
                    boardMap.itemImageId = images[Constants.itemImageIndex]?.id
                    boardMap.startImageId = images[Constants.startImageIndex]?.id
                    boardMap.wallImageId = images[Constants.wallImageIndex]?.id
                    val board = ApiHelper.createBoard(boardMap)
                    for (tile in tiles!!) {
                        tile.id = 0
                        tile.boardId = board!!.id
                        ApiHelper.createTile(tile)
                    }
                }
            }

            val intent = Intent(myContext, TasksActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.deleteTask.setOnClickListener {//TODO opytaj sa ze ci ozaj?
            CoroutineScope(Dispatchers.IO).launch {
                deleteTaskBoards()
                val task = ApiHelper.deleteTask(taskId!!)
                withContext(Dispatchers.Main) {
                    if (task == null) {
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(myContext, TasksActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            binding.selectImagesPanel.visibility = View.VISIBLE
            //supportFragmentManager.popBackStack()
        }

        binding.backButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.deleteText.text = Constants.getBackCreatingTaskNotSavedString()
        }

        binding.confirmDelete.setOnClickListener {
            val intent = Intent(myContext, TasksActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.closeDeletePanel.setOnClickListener{
            binding.deletePanel.visibility = View.GONE
        }

        binding.numOfColumnsPicker.setOnValueChangedListener{ _, _, newVal ->
            binding.numOfRowsPicker.value = newVal
        }

        binding.numOfRowsPicker.setOnValueChangedListener { _, _, newVal ->
            binding.numOfColumnsPicker.value = newVal
        }
    }

    private fun loadGalleryFragment(savedInstanceState: Bundle?){
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    private fun loadViewModel(){
        viewModel = ViewModelProvider(myContext)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(myContext) { image ->
            Constants.ImageGridImages[viewModel.getChangingImageIndex().value!!] = image
            imageGridViewAdapter.notifyDataChanged()

            binding.fragmentLayout.visibility = View.GONE
            binding.selectImagesPanel.visibility = View.VISIBLE
        }

        viewModel.getStartingPosition().observe(myContext) { position ->
            boardMaps[currentBoardIndex].startIndex = position
            setBoard(boardMaps[currentBoardIndex].columns)
        }
    }

    private fun loadBoardsIfExist() {
        CoroutineScope(Dispatchers.IO).launch {
            val boards = ApiHelper.getTaskBoards(taskId!!) as MutableList<Board>?
            if (!boards.isNullOrEmpty()) {
                for (board in boards) {
                    board.tiles = ApiHelper.getBoardTiles(board.id) as MutableList<Tile>?
                }
                boardMaps = boards
                boardMap = boards[0].tiles!!
                currentBoardIndex = 0
                withContext(Dispatchers.Main) {
                    setNextBoardButtonVisibility()
                    setPrevBoardButtonVisibility()
                    binding.mainPanel.visibility = View.VISIBLE
                    binding.boardSizePickerPanel.visibility = View.GONE
                    setBoard(boards[0].columns)
                }
            }
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
        boardAdapter = BoardCreatingAdapter(myContext, boardMap, boardMaps[currentBoardIndex])
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

    private suspend fun deleteTaskBoards(){
        val boards = ApiHelper.getTaskBoards(taskId!!) as MutableList<Board>?
        if (!boards.isNullOrEmpty()) {
            for (board in boards) {
                val tiles = ApiHelper.getBoardTiles(board.id)
                if (!tiles.isNullOrEmpty()) {
                    for (tile in tiles) {
                        ApiHelper.deleteTile(tile.id)
                    }
                }
                ApiHelper.deleteBoard(board.id)
            }
        }
    }
}