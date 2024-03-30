package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.adapters.ClassroomTasksAdapter
import com.example.eduenvi.databinding.ActivityClassTasksBinding
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClassTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassTasksBinding
    private var _creatingNew: Boolean? = null
    private var tasks: MutableList<Task>? = null
    private lateinit var adapter: ClassroomTasksAdapter
    private var settingDeadline = false
    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassTasksBinding.inflate(layoutInflater)
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
        binding.className.text = classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }
        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, ClassGroupsActivity::class.java)
            startActivity(intent)
        }
        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, ClassStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            binding.tasksPanel.visibility = View.VISIBLE
            binding.taskName.text = null
            binding.dateTimeTaskDeadline.text = Constants.SetButtonText
            binding.dateTimeTaskVisibleFrom.text = Constants.SetButtonText
            addTaskTypesToList()
        }

        binding.editButton.setOnClickListener {
            val task = Constants.Task
            _creatingNew = false
            binding.editPanel.visibility = View.GONE
            binding.tasksPanel.visibility = View.VISIBLE
            binding.taskName.setText(task.name)
            if (task.deadline != null) binding.dateTimeTaskDeadline.text = task.deadline.toString()
            if (task.visibleFrom != null) binding.dateTimeTaskVisibleFrom.text =
                task.visibleFrom.toString()
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
            closeDateTimePanel()
            val hour = if (binding.timePicker.hour.toString().length == 1) String.format("0%d", binding.timePicker.hour) else binding.timePicker.hour
            val minute = if (binding.timePicker.minute.toString().length == 1) String.format("0%d", binding.timePicker.minute) else binding.timePicker.minute
            val datetime = String.format("%d:%d %d.%d.%d", hour, minute, binding.datePicker.dayOfMonth, binding.datePicker.month, binding.datePicker.year)
            if (settingDeadline) binding.dateTimeTaskDeadline.text = datetime
            else binding.dateTimeTaskVisibleFrom.text = datetime
            //todo zapis vybraty datum a cas, zapamataj si ho v premennej
        }

        binding.saveButton.setOnClickListener {
            val validName = validName()
            val validType = validType()
            if (validName) {
                val task =
                    Task(
                        0,
                        Constants.Teacher.id,
                        0,//TODO tasktype z vyberu, kt musim spravit este
                        binding.taskName.toString(),
                        "",//TODO nastavit text, spravit okinko, treba text?
                        null,//TODO nahranie obrazka, spravit okinko
                        null,//TODO spravit premennu z dat co mam
                        null//TODO spravit premennu z dat co mam
                    )
                var res: Task?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNew == false) {
                        task.id = Constants.Task.id
                        res = ApiHelper.updateTask(task.id, task)
                        tasks!!.remove(Constants.Task)//TODO ak sa podaril update else toast nepodarilo sa
                    } else {
                        res = ApiHelper.createTask(task)
                    }
                    Constants.Task = task
                    tasks!!.add(task)//TODO ak res nie je null else toast nepodarilo sa vytvorit/updatnut
                    withContext(Dispatchers.Main) {
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
                ApiHelper.deleteTask(Constants.Task.id)
                tasks!!.remove(Constants.Task)//TODO ak sa podaril delete else toast nepodarilo sa
                withContext(Dispatchers.Main) {
                    adapter.notifyDataChanged()
                }
            }
            val intent = Intent(this, ClassTasksActivity::class.java)
            startActivity(intent)
        }

        binding.closeTaskPanel.setOnClickListener { binding.tasksPanel.visibility = View.GONE }
        binding.dateTimePickerPanel.setOnClickListener{ closeDateTimePanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.taskName.addTextChangedListener { binding.taskNameTextInputLayout.error = null }
    }

    private fun addTaskTypesToList() {
        val context = this
        CoroutineScope(Dispatchers.IO).launch {
            val taskTypes = ApiHelper.getAllTaskTypes()
            withContext(Dispatchers.Main) {
                //TODO pridat aj plusko? pridanie nahranie noveho typu, aj niekde inde? len tu alebo len niekde inde
                taskTypes?.forEach{ taskType ->
                    val tagName = taskType.name
                    val chip = Chip(context)
                    chip.isCheckable = true
                    chip.text = tagName
                    chip.layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    chip.setOnCloseIconClickListener {
                        chip.isChecked = true
                        //TODO oznac ho, niekde treba nastavit, ze sa da vzdy vybrat len jeden chip, treba aj zapamatat, ze aky je to typ, asi zas len do konstant
                    }
                    binding.chipTaskType.addView(chip)
                }
            }
        }
    }

    private fun closeTaskPanel() {
        binding.tasksPanel.visibility = View.GONE
        binding.taskNameTextInputLayout.error = null
        binding.taskName.text = null
        //TODO vyprazdnit typy uloh...
    }

    private fun closeDateTimePanel(){
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

    private fun validType(): Boolean{
        return true //zisti, ci je vybraty nejaky chip
    }
}