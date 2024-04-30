package com.example.eduenvi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.ClassroomTasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityClassroomTasksBinding
import com.example.eduenvi.models.ClassroomTask
import com.example.eduenvi.models.GroupTask
import com.example.eduenvi.models.StudentTask
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

//TODO ked odstranim tak odstarn studentTask aj classroom task aj grouptask
//TODO chcek ci je satum od mensi ako do, odstranenie datumu
//TODO daj o uroven vyssie, nech vytvara ulohu mimo triedy, do triedy potom len ulohy, kt maju ziaci/skupiny, ked a ne kliknem ze upravit tak mozem meit ziakova skupiny ako pri ziakovych skupinach ulohach skupinovych ziakoch ulohach
class ClassroomTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomTasksBinding
    private var _creatingNew = false
    private var tasks = mutableListOf<Task>()
    private lateinit var adapter: ClassroomTasksAdapter
    private var settingDeadline = false
    private val myContext = this
    private var deadline: Date? = null
    private var visibleFrom: Date? = null
    private var imageId: Int? = null
    private lateinit var viewModel: MyViewModel
    private val fragment = ImageGalleryFragment()
    private var soloTask = false
    private var groupTask = false
    private var freeTask = true

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadGalleryFragment(savedInstanceState)
        loadViewModel()
        loadTasksToLayout()

        binding.classroomName.text = Constants.Classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomsActivity::class.java)
            startActivity(intent)
        }
        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, ClassroomGroupsActivity::class.java)
            startActivity(intent)
        }
        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, ClassroomStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            setTaskTypeLayout()
            setDateTimeText()
            imageId = null
            deadline = null
            visibleFrom = null
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
            setDateTimeText()
            imageId = Constants.Task.imageId
            deadline = Constants.Task.deadline
            visibleFrom = Constants.Task.visibleFrom
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

        binding.dateTimeTaskVisibleFrom.setOnClickListener {
            settingDeadline = false
            binding.dateTimePickerPanel.visibility = View.VISIBLE
        }

        binding.dateTimeTaskDeadline.setOnClickListener {
            settingDeadline = true
            binding.dateTimePickerPanel.visibility = View.VISIBLE
        }

        binding.setDate.setOnClickListener {
            binding.dateLayout.visibility = View.GONE
            binding.timeLayout.visibility = View.VISIBLE
        }

        binding.setTime.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            cal.set(Calendar.YEAR, binding.datePicker.year)
            cal.set(Calendar.MONTH, binding.datePicker.month)
            cal.set(Calendar.DAY_OF_MONTH, binding.datePicker.dayOfMonth)
            cal.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            cal.set(Calendar.MINUTE, binding.timePicker.minute)
            val sdf = SimpleDateFormat("dd. MM. yyyy hh:mm")
            val datetime: String = sdf.format(cal.time)
            if (settingDeadline) {
                binding.dateTimeTaskDeadline.text = datetime
                deadline = cal.time
            } else {
                binding.dateTimeTaskVisibleFrom.text = datetime
                visibleFrom = cal.time
            }
            closeDateTimePanel()
        }

        binding.soloTaskButton.setOnClickListener {
            soloTask = true
            groupTask = false
            freeTask = false
            binding.soloTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.Secondary))
            binding.groupTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
            binding.freeTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
        }
        binding.groupTaskButton.setOnClickListener {
            soloTask = false
            groupTask = true
            freeTask = false
            binding.soloTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
            binding.groupTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.Secondary))
            binding.freeTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
        }
        binding.freeTaskButton.setOnClickListener {
            soloTask = false
            groupTask = false
            freeTask = true
            binding.soloTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
            binding.groupTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
            binding.freeTaskButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.Secondary))
        }

        binding.saveButton.setOnClickListener {
            val validName = validName()
            val validType = validType()
            if (validName && validType) {
                val teacherId = Constants.Teacher.id
                val taskTypeId = Constants.TaskType.id
                val name = binding.taskName.text.toString()
                val text = binding.text.text.toString()
                val task = Task(0, teacherId, taskTypeId, name, text, imageId, deadline, visibleFrom)
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

        binding.closeTaskPanel.setOnClickListener { closeTaskPanel() }
        binding.dateTimePickerPanel.setOnClickListener { closeDateTimePanel() }
        binding.closeDate.setOnClickListener { closeDateTimePanel() }
        binding.closeTime.setOnClickListener { closeDateTimePanel() }
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
            val t = ApiHelper.getTasksInClassroom(Constants.Classroom.id)
            tasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = ClassroomTasksAdapter(myContext, tasks)
                binding.tasksLayout.adapter = adapter
            }
        }
    }

    private fun setDateTimeText() {
        binding.dateTimeTaskDeadline.text = Constants.SetButtonText
        binding.dateTimeTaskVisibleFrom.text = Constants.SetButtonText
        if (!_creatingNew) {
            if (Constants.Task.deadline != null) {
                binding.dateTimeTaskDeadline.text = Constants.Task.deadline.toString()
            }
            if (Constants.Task.visibleFrom != null) {
                binding.dateTimeTaskVisibleFrom.text = Constants.Task.visibleFrom.toString()
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
            if (res != null && _creatingNew) {
                ApiHelper.createClassroomTask(ClassroomTask(Constants.Classroom.id, res!!.id))
                if (soloTask) setTaskToStudentsInClassroom(task)
                else if (groupTask) setTaskToGroupsInClassroom(task)
            }
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

    private suspend fun setTaskToStudentsInClassroom(task: Task){
        val students = ApiHelper.getStudentsInClassroom(Constants.Classroom.id)
        for (student in students!!){
            ApiHelper.createStudentTask(StudentTask(student.id, task.id))
        }
    }

    private suspend fun setTaskToGroupsInClassroom(task: Task){
        val groups = ApiHelper.getGroupsInClassroom(Constants.Classroom.id)
        for (group in groups!!){
            ApiHelper.createGroupTask(GroupTask(group.id, task.id))
        }
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

    private fun closeDateTimePanel() {
        binding.dateTimePickerPanel.visibility = View.GONE
        binding.dateLayout.visibility = View.VISIBLE
        binding.timeLayout.visibility = View.GONE
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