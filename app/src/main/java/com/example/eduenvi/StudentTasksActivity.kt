package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.StudentsTasksAdapter
import com.example.eduenvi.databinding.ActivityStudentTasksBinding
import com.example.eduenvi.models.StudentTask
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityStudentTasksBinding

    private var _delFromStudent: HashSet<Task> = HashSet()
    private var _addToStudent: HashSet<Task> = HashSet()
    private var tasks: MutableList<Task>? = null
    private lateinit var adapter: StudentsTasksAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val student = Constants.Student
        CoroutineScope(Dispatchers.IO).launch {
            tasks = ApiHelper.getStudentsTasks(student.id) as MutableList<Task>?

            withContext(Dispatchers.Main) {
                if (tasks != null) {
                    adapter = StudentsTasksAdapter(myContext, tasks!!)
                    binding.tasksLayout.adapter = adapter
                }
            }
        }

        binding.studentName.text = "${student.name} ${student.lastName}"

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, StudentGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.deleteStudentTask(student.id, Constants.Task.id)
                tasks!!.remove(Constants.Task)
                withContext(Dispatchers.Main) {
                    adapter.notifyDataChanged()
                }
            }
        }

        binding.addButton.setOnClickListener {
            setActiveTasksPanel()
        }

        binding.saveButton.setOnClickListener {
            manageTasks()
            closeTaskPanel()
        }

        binding.closeTaskPanel.setOnClickListener { closeTaskPanel() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun closeTaskPanel() {
        binding.tasksPanel.visibility = View.GONE
        empty()
    }

    private fun empty() {
        for (i in binding.chipGroupIn.childCount - 1 downTo 0) {
            val chip = binding.chipGroupIn.getChildAt(i)
            binding.chipGroupIn.removeView(chip)
        }

        for (i in binding.chipGroupNotIn.childCount - 1 downTo 0) {
            val chip = binding.chipGroupNotIn.getChildAt(i)
            binding.chipGroupNotIn.removeView(chip)
        }
    }

    private fun setActiveTasksPanel() {
        binding.tasksPanel.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val tasksInStudent = ApiHelper.getStudentsTasks(Constants.Student.id)
            val tasksNotInStudent = ApiHelper.getTasksFromTeacherNotInStudent(
                Constants.Teacher.id,
                Constants.Student.id
            )

            withContext(Dispatchers.Main) {
                addTasksToLists(tasksInStudent, tasksNotInStudent)
            }
        }
    }

    private fun addTasksToLists(tasksInStudent: List<Task>?, tasksNotInStudent: List<Task>?) {
        tasksInStudent?.forEach { task ->
            addTaskToList(task, binding.chipGroupIn, true)
        }
        tasksNotInStudent?.forEach { task ->
            addTaskToList(task, binding.chipGroupNotIn, false)
        }
    }

    private fun addTaskToList(task: Task, chipGroup: ChipGroup, isInStudent: Boolean) {
        var addedInStudent = isInStudent
        val tagName = task.name
        val chip = Chip(this)
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (isInStudent) chip.setCloseIconResource(R.drawable.baseline_close_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_24)
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            if (addedInStudent) {
                chip.setCloseIconResource(R.drawable.baseline_add_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInStudent = false
                if (isInStudent) _delFromStudent.add(task)
                else _addToStudent.remove(task)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInStudent = true
                if (!isInStudent) _addToStudent.add(task)
                else _delFromStudent.remove(task)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            for (task in _addToStudent) {
                val newTask = ApiHelper.createStudentTask(StudentTask(Constants.Student.id, task.id))
                if (newTask != null) tasks!!.add(task) //TODO else toast nepodarilo sa
            }
            for (task in _delFromStudent) {
                ApiHelper.deleteStudentTask(Constants.Student.id, task.id)
                tasks!!.remove(task) //TODO if delete successful then... else toast neepodarilo sa
            }
            withContext(Dispatchers.Main) {
                adapter.notifyDataChanged()
                _delFromStudent = HashSet()
                _addToStudent = HashSet()
            }
        }
    }
}