package com.example.eduenvi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.TasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityTasksBinding
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityTasksBinding
    private var _creatingNew = false
    private var tasks = mutableListOf<Task>()
    private lateinit var adapter: TasksAdapter
    private val myContext = this
    private var imageId: Int? = null
    private lateinit var viewModel: MyViewModel
    private val fragment = ImageGalleryFragment()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadGalleryFragment(savedInstanceState)
        loadViewModel()
        loadTasksToLayout()

        binding.menuButton.setOnClickListener {
            binding.menuPanel.visibility = if (binding.menuPanel.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        binding.classroomsButton.setOnClickListener {
            val intent = Intent(this, ClassroomsActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            setTaskTypeLayout()
            imageId = null
            binding.taskName.text = null
            binding.text.text = null
            binding.saveButton.text = Constants.SaveButtonTextCreate
            Constants.imageManager.setImage("", this, binding.taskImage)
            binding.taskPanel.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            setTaskTypeLayout()
            imageId = Constants.Task.imageId
            binding.taskName.setText(Constants.Task.name)
            binding.text.setText(Constants.Task.text)
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            Constants.imageManager.setImage(Constants.Task.imageId, this, binding.taskImage)
            binding.taskPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.mainPanel.visibility = View.GONE

        }

        binding.editTaskImage.setOnClickListener {
            fragment.load()
            binding.fragmentLayout.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            val validName = validName()
            val validType = validType()
            if (validName && validType) {
                val teacherId = Constants.Teacher.id
                val taskTypeId = Constants.TaskType.id
                val name = binding.taskName.text.toString()
                val text = binding.text.text.toString()
                val task = Task(0, teacherId, taskTypeId, name, text, imageId)
                saveTask(task)
                closeTaskPanel()
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteGroupString(Constants.Group)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteTask(Constants.Task.id)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        tasks.remove(Constants.Task)
                        adapter.notifyDataChanged()
                    } else {
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    }
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            //TODO mozno ho nejak killnut ten fragment vsade
            binding.editPanel.visibility = View.VISIBLE
            //supportFragmentManager.popBackStack()
        }

        binding.menuPanel.setOnClickListener { binding.menuPanel.visibility = View.GONE }
        binding.closeTaskPanel.setOnClickListener { closeTaskPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.taskName.addTextChangedListener { binding.taskNameTextInputLayout.error = null }
    }

    private fun loadGalleryFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    private fun loadViewModel() {
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(this) { image ->
            imageId = image.id
            Constants.imageManager.setImage(image.url, this, binding.taskImage)
            binding.fragmentLayout.visibility = View.GONE
        }
    }

    private fun loadTasksToLayout() {
        CoroutineScope(Dispatchers.IO).launch {
            val t = ApiHelper.getTeacherTasks(Constants.Teacher.id)
            tasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = TasksAdapter(myContext, tasks)
                binding.tasksLayout.adapter = adapter
            }
        }
    }

    private fun setTaskTypeLayout() {
        if (_creatingNew) {
            binding.taskTypeLayout.visibility = View.VISIBLE
            binding.updatingTaskTypeLayout.visibility = View.GONE
            addTaskTypesToList()
        } else {
            binding.taskTypeLayout.visibility = View.GONE
            binding.updatingTaskTypeLayout.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                val name = getTaskTypeName(Constants.Task.taskTypeId)
                withContext(Dispatchers.Main){
                    binding.updatingTaskTypeText.text = name
                }
            }
        }
    }

    private fun addTaskTypesToList() {
        CoroutineScope(Dispatchers.IO).launch {
            val taskTypes = ApiHelper.getAllTaskTypes()
            withContext(Dispatchers.Main) {
                taskTypes?.forEach { taskType ->
                    val tagName = taskType.name
                    val chip = Chip(myContext)
                    chip.isCheckable = true
                    chip.text = tagName
                    chip.layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    chip.setOnClickListener {
                        binding.chipTaskTypeError.visibility = View.GONE
                        Constants.TaskType = taskType
                    }
                    binding.chipTaskType.addView(chip)
                }
            }
        }
    }

    private fun saveTask(task: Task) {
        var res: Task?
        CoroutineScope(Dispatchers.IO).launch {
            res = if (_creatingNew) saveNewTask(task) else updateTask(task)

            withContext(Dispatchers.Main) {
                if (res != null) {
                    tasks.add(res!!)
                    adapter.notifyDataChanged()
                    val intent = Intent(myContext, Constants.TaskTypeCreatingActivity[res!!.taskTypeId])
                    intent.putExtra("TASK_ID", res!!.id)
                    myContext.startActivity(intent)
                } else {
                    Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun saveNewTask(task: Task) = ApiHelper.createTask(task)

    private suspend fun updateTask(task: Task): Task? {
        task.id = Constants.Task.id
        val res = ApiHelper.updateTask(task.id, task)
        if (res != null) tasks.remove(Constants.Task)
        return res
    }

    private suspend fun getTaskTypeName(id: Int): String {
        return ApiHelper.getTaskType(id)?.name ?: "id: $id"
    }

    private fun closeTaskPanel() {
        binding.taskPanel.visibility = View.GONE
        binding.mainPanel.visibility = View.VISIBLE
        binding.taskNameTextInputLayout.error = null
        binding.taskName.text = null
        binding.text.text = null
        for (i in binding.chipTaskType.childCount - 1 downTo 0) {
            val chip = binding.chipTaskType.getChildAt(i)
            binding.chipTaskType.removeView(chip)
        }
    }

    private fun validName(): Boolean {
        if (binding.taskName.text.toString().length < Constants.MinimalTaskNameLength) {
            binding.taskNameTextInputLayout.error = Constants.WrongTaskNameFormatMessage
            return false
        }
        return true
    }

    private fun validType(): Boolean {
        binding.chipTaskTypeError.visibility = View.GONE
        if (!_creatingNew) return true
        if (binding.chipTaskType.checkedChipIds.size == 1) return true
        binding.chipTaskTypeError.text = Constants.WrongTaskTypeNotSelected
        binding.chipTaskTypeError.visibility = View.VISIBLE
        return false
    }

}