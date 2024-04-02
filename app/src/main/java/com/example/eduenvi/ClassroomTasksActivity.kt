package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.adapters.ClassroomTasksAdapter
import com.example.eduenvi.databinding.ActivityClassroomTasksBinding
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class ClassroomTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomTasksBinding
    private var _creatingNew: Boolean? = null
    private var tasks: MutableList<Task>? = null
    private lateinit var adapter: ClassroomTasksAdapter
    private var settingDeadline = false
    private val myContext = this
    private var deadlineDate: Date? = null
    private var visibleFromDate: Date? = null
    private var imageId :Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            tasks = ApiHelper.getTasksInClassroom(Constants.Classroom.id) as MutableList<Task>?

            withContext(Dispatchers.Main) {
                if (tasks != null) {
                    adapter = ClassroomTasksAdapter(myContext, tasks!!)
                    binding.tasksLayout.adapter = adapter
                }
            }
        }
        val classroom = Constants.Classroom
        binding.classroomName.text = classroom.name

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
            binding.taskPanel.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
            binding.taskName.text = null
            binding.dateTimeTaskDeadline.text = Constants.SetButtonText
            binding.dateTimeTaskVisibleFrom.text = Constants.SetButtonText
            addTaskTypesToList()
        }

        binding.editButton.setOnClickListener {
            imageId = Constants.Task.imageId
            _creatingNew = false
            binding.editPanel.visibility = View.GONE
            binding.mainPanel.visibility = View.GONE
            binding.taskPanel.visibility = View.VISIBLE
            binding.taskName.setText(Constants.Task.name)
            if (Constants.Task.deadline != null)
                binding.dateTimeTaskDeadline.text = Constants.Task.deadline.toString()
            if (Constants.Task.visibleFrom != null)
                binding.dateTimeTaskVisibleFrom.text = Constants.Task.visibleFrom.toString()
        }

        binding.editTaskImage.setOnClickListener {
            //TODO otvor panel na vyberanie obrazkov, tam bude nejake tlacidlo uloz a v nom spravit ak nove vytvor a nastav imageId na nove ak vybera uz z db tak tiez nastav imageId
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
            binding.setDate.visibility = View.GONE
            binding.setTime.visibility = View.VISIBLE
            binding.datePicker.visibility = View.GONE
            binding.timePicker.visibility = View.VISIBLE
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
                deadlineDate = cal.time
            }
            else {
                binding.dateTimeTaskVisibleFrom.text = datetime
                visibleFromDate = cal.time
            }
            closeDateTimePanel()
        }

        binding.saveButton.setOnClickListener {
            val validName = validName()
            val validType = validType()
            if (validName && validType) {
                val task =
                    Task(
                        0,
                        Constants.Teacher.id,
                        Constants.TaskType.id,
                        binding.taskName.toString(),
                        "",//TODO nastavit text, spravit okinko, treba text?
                        imageId,
                        deadlineDate,
                        visibleFromDate
                    )
                var res: Task?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNew == false) {
                        task.id = Constants.Task.id
                        res = ApiHelper.updateTask(task.id, task)
                        if (res != null) if (tasks != null) tasks!!.remove(Constants.Task)
                    } else {
                        res = ApiHelper.createTask(task)
                    }
                    Constants.Task = task
                    withContext(Dispatchers.Main) {
                        if (res != null) if (tasks != null) tasks!!.add(task)
                        else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG)
                            .show()
                        adapter.notifyDataChanged()
                    }
                }
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
                val result = ApiHelper.deleteTask(Constants.Task.id)
                withContext(Dispatchers.Main) {
                    if (result != null) if (tasks != null) tasks!!.remove(Constants.Task)
                    else Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    adapter.notifyDataChanged()
                    binding.deletePanel.visibility = View.GONE
                }
            }
            //val intent = Intent(this, ClassroomTasksActivity::class.java)
            //startActivity(intent)
        }

        binding.closeTaskPanel.setOnClickListener { closeTaskPanel() }
        binding.dateTimePickerPanel.setOnClickListener { closeDateTimePanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.taskName.addTextChangedListener { binding.taskNameTextInputLayout.error = null }
    }

    private fun addTaskTypesToList() {
        CoroutineScope(Dispatchers.IO).launch {
            val taskTypes = ApiHelper.getAllTaskTypes()
            withContext(Dispatchers.Main) {
                //TODO pridat aj plusko? pridanie nahranie noveho typu, aj niekde inde? len tu alebo len niekde inde
                taskTypes?.forEach { taskType ->
                    val tagName = taskType.name
                    val chip = Chip(myContext)
                    chip.isCheckable = true
                    chip.text = tagName
                    chip.layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    chip.setOnClickListener{
                        binding.chipTaskTypeError.visibility = View.GONE
                        Constants.TaskType = taskType
                    }
                    binding.chipTaskType.addView(chip)
                }
            }
        }
    }

    private fun closeTaskPanel() {
        binding.taskPanel.visibility = View.GONE
        binding.mainPanel.visibility = View.VISIBLE
        binding.taskNameTextInputLayout.error = null
        binding.taskName.text = null
        for (i in binding.chipTaskType.childCount - 1 downTo 0) {
            val chip = binding.chipTaskType.getChildAt(i)
            binding.chipTaskType.removeView(chip)
        }
    }

    private fun closeDateTimePanel() {
        binding.dateTimePickerPanel.visibility = View.GONE
        binding.setDate.visibility = View.VISIBLE
        binding.setTime.visibility = View.GONE
        binding.datePicker.visibility = View.VISIBLE
        binding.timePicker.visibility = View.GONE
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
        if (binding.chipTaskType.checkedChipIds.size == 1) return true
        else {
            binding.chipTaskTypeError.setText(Constants.WrongTaskTypeNotSelected)
            binding.chipTaskTypeError.visibility = View.VISIBLE
            return false
        }
    }
}