package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.StudentGroupsAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityStudentGroupsBinding
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.StudentGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentGroupsActivity : AppCompatActivity() {

    lateinit var binding: ActivityStudentGroupsBinding

    private var _delFromStudent: HashSet<Group> = HashSet()
    private var _addToStudent: HashSet<Group> = HashSet()
    private var groups = mutableListOf<Group>()
    private lateinit var adapter: StudentGroupsAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadGroupsToLayout()

        binding.studentName.text = "${Constants.Student.firstName} ${Constants.Student.lastName}"

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, StudentTasksActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteStudentGroup(Constants.Student.id, Constants.Group.id)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        groups.remove(Constants.Group)
                        adapter.notifyDataChanged()
                    } else {
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    }
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.addButton.setOnClickListener {
            setActiveGroupsPanel()
        }

        binding.saveButton.setOnClickListener {
            manageGroups()
            closeGroupsPanel()
        }

        binding.closeGroupsPanel.setOnClickListener { closeGroupsPanel() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun loadGroupsToLayout(){
        CoroutineScope(Dispatchers.IO).launch {
            val g = ApiHelper.getStudentsGroups(Constants.Student.id)
            groups = if (g == null) mutableListOf() else g as MutableList<Group>

            withContext(Dispatchers.Main) {
                adapter = StudentGroupsAdapter(myContext, groups)
                binding.groupsLayout.adapter = adapter

            }
        }
    }

    private fun closeGroupsPanel() {
        binding.groupsPanel.visibility = View.GONE
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

    private fun setActiveGroupsPanel() {
        binding.groupsPanel.visibility = View.VISIBLE
        binding.mainPanel.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val groupsInStudent = ApiHelper.getStudentsGroups(Constants.Student.id)
            val groupsNotInStudent = ApiHelper.getGroupsFromInClassroomNotInStudent(
                Constants.Classroom.id,
                Constants.Student.id
            )

            withContext(Dispatchers.Main) {
                addGroupsToLists(groupsInStudent, groupsNotInStudent)
            }
        }
    }

    private fun addGroupsToLists(groupsInStudent: List<Group>?, groupsNotInStudent: List<Group>?) {
        groupsInStudent?.forEach { group ->
            addGroupToList(group, binding.chipGroupIn, true)
        }
        groupsNotInStudent?.forEach { group ->
            addGroupToList(group, binding.chipGroupNotIn, false)
        }
    }

    private fun addGroupToList(group: Group, chipGroup: ChipGroup, isInGroup: Boolean) {
        var addedInGroup = isInGroup
        val tagName = group.name
        val chip = Chip(this)
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (isInGroup) chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)

        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            //Constants.Student = student
            if (addedInGroup) {
                chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInGroup = false
                if (isInGroup) _delFromStudent.add(group)
                else _addToStudent.remove(group)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInGroup = true
                if (!isInGroup) _addToStudent.add(group)
                else _delFromStudent.remove(group)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageGroups() {
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (group in _addToStudent) {
                val newGroup =
                    ApiHelper.createStudentGroup(StudentGroup(Constants.Student.id, group.id))
                if (newGroup != null) groups.add(group)
                else showSaveToast = true
            }
            for (group in _delFromStudent) {
                val res = ApiHelper.deleteStudentGroup(Constants.Student.id, group.id)
                if (res != null) groups.remove(group)
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