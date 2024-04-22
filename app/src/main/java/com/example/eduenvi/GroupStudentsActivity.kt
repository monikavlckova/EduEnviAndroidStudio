package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.GroupStudentsAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityGroupStudentsBinding
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.StudentGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupStudentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityGroupStudentsBinding

    private var _delFromGroup: HashSet<Student> = HashSet()
    private var _addToGroup: HashSet<Student> = HashSet()
    private var students = mutableListOf<Student>()
    private lateinit var adapter: GroupStudentsAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val group = Constants.Group
        CoroutineScope(Dispatchers.IO).launch {
            students = ApiHelper.getStudentsInGroup(group.id) as MutableList<Student>

            withContext(Dispatchers.Main) {
                adapter = GroupStudentsAdapter(myContext, students)
                binding.studentsLayout.adapter = adapter

            }
        }

        binding.groupName.text = group.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, GroupTasksActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteStudentGroup(Constants.Student.id, group.id)
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
            //val intent = Intent(this, GroupStudentsActivity::class.java)
            //startActivity(intent)
        }

        binding.closeStudentsPanel.setOnClickListener { closeStudentsPanel() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
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
            val studentsInGroup = ApiHelper.getStudentsInGroup(Constants.Group.id)
            val studentsNotInGroup = ApiHelper.getStudentsFromClassroomNotInGroup(
                Constants.Classroom.id,
                Constants.Group.id
            )

            withContext(Dispatchers.Main) {
                addStudentsToLists(studentsInGroup, studentsNotInGroup)
            }
        }
    }

    private fun addStudentsToLists(
        studentsInGroup: List<Student>?,
        studentsNotInGroup: List<Student>?
    ) {
        studentsInGroup?.forEach { student ->
            addStudentToList(student, binding.chipGroupIn, true)
        }
        studentsNotInGroup?.forEach { student ->
            addStudentToList(student, binding.chipGroupNotIn, false)
        }
    }

    private fun addStudentToList(student: Student, chipGroup: ChipGroup, isInGroup: Boolean) {
        var addedInGroup = isInGroup
        val tagName = student.firstName + " " + student.lastName
        val chip = Chip(this)
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (isInGroup) chip.setCloseIconResource(R.drawable.baseline_close_dark_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_24)

        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            if (addedInGroup) {
                chip.setCloseIconResource(R.drawable.baseline_add_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInGroup = false
                if (isInGroup) _delFromGroup.add(student)
                else _addToGroup.remove(student)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_dark_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInGroup = true
                if (!isInGroup) _addToGroup.add(student)
                else _delFromGroup.remove(student)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageStudents() {
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (student in _addToGroup) {
                val res = ApiHelper.createStudentGroup(StudentGroup(student.id, Constants.Group.id))
                if (res != null) students.add(student)
                else showSaveToast = true
            }
            for (student in _delFromGroup) {
                val res = ApiHelper.deleteStudentGroup(student.id, Constants.Group.id)
                if (res != null) students.remove(student)
                else showDeleteToast = true
            }
            withContext(Dispatchers.Main) {
                if (showSaveToast)
                    Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                if (showDeleteToast)
                    Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()

                adapter.notifyDataChanged()
                _delFromGroup = HashSet()
                _addToGroup = HashSet()
            }
        }
    }
}