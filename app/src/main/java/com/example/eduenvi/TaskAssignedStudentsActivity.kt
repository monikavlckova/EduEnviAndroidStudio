package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.TaskAssignedStudentsAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityTaskAssignedStudentsBinding
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.StudentAssignedTask
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAssignedStudentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskAssignedStudentsBinding

    private var _delFromTask: HashSet<Student> = HashSet()
    private var _addToTask: HashSet<Student> = HashSet()
    private var students = mutableListOf<Student>()
    private lateinit var adapter: TaskAssignedStudentsAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAssignedStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadStudentsToLayout()

        binding.chipGroupIn.chipSpacingVertical = 1
        binding.chipGroupNotIn.chipSpacingVertical = 1

        binding.taskName.text = Constants.Task.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomTasksActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, TaskAssignedGroupsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteStudentAssignedTask(Constants.Student.id, Constants.Task.assignedTaskId!!)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        students.remove(Constants.Student)
                        adapter.notifyDataChanged()

                    } else {
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    }
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.addButton.setOnClickListener {
            setActiveStudentsPanel()
        }

        binding.saveButton.setOnClickListener {
            manageStudents()
            closeStudentsPanel()
        }

        binding.closeStudentsPanel.setOnClickListener { closeStudentsPanel() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun loadStudentsToLayout(){
        CoroutineScope(Dispatchers.IO).launch {
            val s = ApiHelper.getStudentsInAssignedTask(Constants.Task.assignedTaskId!!)
            students = if (s == null) mutableListOf() else s as MutableList<Student>

            withContext(Dispatchers.Main) {
                adapter = TaskAssignedStudentsAdapter(myContext, students)
                binding.studentsLayout.adapter = adapter
            }
        }
    }

    private fun closeStudentsPanel() {
        binding.studentsPanel.visibility = View.GONE
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

    private fun setActiveStudentsPanel() {
        binding.studentsPanel.visibility = View.VISIBLE
        binding.mainPanel.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val studentsInTask = ApiHelper.getStudentsInAssignedTask(Constants.Task.assignedTaskId!!)
            val studentsNotInTask = ApiHelper.getStudentsFromClassroomNotInAssignedTask(Constants.Classroom.id, Constants.Task.assignedTaskId!!)

            withContext(Dispatchers.Main) {
                addStudentsToLists(studentsInTask, studentsNotInTask)
            }
        }
    }

    private fun addStudentsToLists(studentsInTask: List<Student>?, studentsNotInTask: List<Student>?) {
        studentsInTask?.forEach { student ->
            addStudentToList(student, binding.chipGroupIn, true)
        }
        studentsNotInTask?.forEach { student ->
            addStudentToList(student, binding.chipGroupNotIn, false)
        }
    }

    private fun addStudentToList(student: Student, chipGroup: ChipGroup, isInTask: Boolean) {
        var addedInTask = isInTask
        val tagName = student.firstName + " " + student.lastName
        val chip = Chip(this)
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (isInTask) chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)

        chip.isCloseIconVisible = true
        chip.setOnClickListener {
            if (addedInTask) {
                chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInTask = false
                if (isInTask) _delFromTask.add(student)
                else _addToTask.remove(student)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInTask = true
                if (!isInTask) _addToTask.add(student)
                else _delFromTask.remove(student)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageStudents() {
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (student in _addToTask) {
                val res = ApiHelper.createStudentAssignedTask(StudentAssignedTask(student.id, Constants.Task.assignedTaskId!!))
                if (res != null) students.add(student)
                else showSaveToast = true
            }
            for (student in _delFromTask) {
                val res = ApiHelper.deleteStudentAssignedTask(student.id, Constants.Task.assignedTaskId!!)
                if (res != null) students.remove(student)
                else showDeleteToast = true
            }
            withContext(Dispatchers.Main) {
                if (showSaveToast)
                    Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                if (showDeleteToast)
                    Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()

                adapter.notifyDataChanged()
                _delFromTask = HashSet()
                _addToTask = HashSet()
            }
        }
    }
}