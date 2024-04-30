package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.StudentTasksAdapter
import com.example.eduenvi.api.ApiHelper
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
    private var tasks = mutableListOf<Task>()
    private lateinit var adapter: StudentTasksAdapter
    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTasksToLayout()

        binding.studentName.text = "${Constants.Student.firstName} ${Constants.Student.lastName}"

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, StudentGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteStudentTask(Constants.Student.id, Constants.Task.id)

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

        binding.addButton.setOnClickListener {
            setActiveTasksPanel()
        }

        binding.saveButton.setOnClickListener {
            manageTasks()
            closeTasksPanel()
        }

        binding.closeTasksPanel.setOnClickListener { closeTasksPanel() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun loadTasksToLayout(){
        CoroutineScope(Dispatchers.IO).launch {
            val t = ApiHelper.getStudentsTasks(Constants.Student.id)
            tasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = StudentTasksAdapter(myContext, tasks)
                binding.tasksLayout.adapter = adapter
            }
        }
    }

    private fun closeTasksPanel() {
        binding.tasksPanel.visibility = View.GONE
        binding.mainPanel.visibility = View.VISIBLE
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
        binding.mainPanel.visibility = View.GONE
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
        if (isInStudent) chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)

        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            if (addedInStudent) {
                chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInStudent = false
                if (isInStudent) _delFromStudent.add(task)
                else _addToStudent.remove(task)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
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
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (task in _addToStudent) {
                val newTask =
                    ApiHelper.createStudentTask(StudentTask(Constants.Student.id, task.id))
                if (newTask != null) tasks.add(task)
                else showSaveToast = true
            }
            for (task in _delFromStudent) {
                val res = ApiHelper.deleteStudentTask(Constants.Student.id, task.id)
                if (res != null) tasks.remove(task)
                else showDeleteToast = true

            }
            withContext(Dispatchers.Main) {
                if (showSaveToast)
                    Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                if (showDeleteToast)
                    Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                adapter.notifyDataChanged()
                _delFromStudent = HashSet()
                _addToStudent = HashSet()
            }
        }
    }
}