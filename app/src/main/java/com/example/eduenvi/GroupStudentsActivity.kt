package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.GroupStudentsAdapter
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
    private var students: MutableList<Student>? = null
    private lateinit var adapter: GroupStudentsAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val group = Constants.Group
        CoroutineScope(Dispatchers.IO).launch {
            students = ApiHelper.getStudentsInGroup(group.id) as MutableList<Student>?

            withContext(Dispatchers.Main) {
                if (students != null) {
                    adapter = GroupStudentsAdapter(myContext, students!!)
                    binding.studentsLayout.adapter = adapter
                }
            }
        }

        binding.groupName.text = group.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, GroupTasksActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val result = ApiHelper.deleteStudentGroup(Constants.Student.id, group.id)
                withContext(Dispatchers.Main) {
                    if (result != null) if (students != null) students!!.remove(Constants.Student)
                    else Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    adapter.notifyDataChanged()
                }
            }
        }

        binding.addButton.setOnClickListener {
            setActiveStudentsPanel()
        }

        binding.saveButton.setOnClickListener {
            manageStudents()
            closeStudentPanel()
            //val intent = Intent(this, GroupStudentsActivity::class.java)
            //startActivity(intent)
        }

        binding.closeStudentPanel.setOnClickListener { closeStudentPanel() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun closeStudentPanel() {
        binding.studentsPanel.visibility = View.GONE
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
        val tagName = student.name + " " + student.lastName
        val chip = Chip(this)
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (isInGroup) chip.setCloseIconResource(R.drawable.baseline_close_24)
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
                chip.setCloseIconResource(R.drawable.baseline_close_24)
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
        CoroutineScope(Dispatchers.IO).launch {
            for (student in _addToGroup) {
                val newStudent =
                    ApiHelper.createStudentGroup(StudentGroup(student.id, Constants.Group.id))
                withContext(Dispatchers.Main) {
                    if (newStudent != null) if (students != null) students!!.add(student)
                    else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG)
                        .show() //TODO napisat konkretne kt student, davat to sem? co ak to bude vyskakovat 10krat
                }
            }
            for (student in _delFromGroup) {
                val result = ApiHelper.deleteStudentGroup(student.id, Constants.Group.id)
                withContext(Dispatchers.Main) {
                    if (result != null) if (students != null) students!!.remove(student)
                    else Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                }
            }
            withContext(Dispatchers.Main) {
                adapter.notifyDataChanged()
                _delFromGroup = HashSet()
                _addToGroup = HashSet()
            }
        }
    }
}