package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.TaskAssignedGroupsAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityTaskAssignedGroupsBinding
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.GroupAssignedTask
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAssignedGroupsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskAssignedGroupsBinding

    private var _delFromTask: HashSet<Group> = HashSet()
    private var _addToTask: HashSet<Group> = HashSet()
    private var groups = mutableListOf<Group>()
    private lateinit var adapter: TaskAssignedGroupsAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAssignedGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadGroupsToLayout()

        binding.chipGroupIn.chipSpacingVertical = 1
        binding.chipGroupNotIn.chipSpacingVertical = 1

        binding.taskName.text = Constants.Task.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomTasksActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, TaskAssignedStudentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteGroupAssignedTask(Constants.Group.id, Constants.Task.assignedTaskId!!)
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
            val g = ApiHelper.getGroupsInAssignedTask(Constants.Task.assignedTaskId!!)
            groups = if (g == null) mutableListOf() else g as MutableList<Group>

            withContext(Dispatchers.Main) {
                adapter = TaskAssignedGroupsAdapter(myContext, groups)
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
            val groupsInTask = ApiHelper.getGroupsInAssignedTask(Constants.Task.assignedTaskId!!)
            val groupsNotInTask = ApiHelper.getGroupsFromClassroomNotInAssignedTask(Constants.Classroom.id, Constants.Task.assignedTaskId!!)

            withContext(Dispatchers.Main) {
                addGroupsToLists(groupsInTask, groupsNotInTask)
            }
        }
    }

    private fun addGroupsToLists(groupsInTask: List<Group>?, groupsNotInTask: List<Group>?) {
        groupsInTask?.forEach { group ->
            addGroupToList(group, binding.chipGroupIn, true)
        }
        groupsNotInTask?.forEach { group ->
            addGroupToList(group, binding.chipGroupNotIn, false)
        }
    }

    private fun addGroupToList(group: Group, chipGroup: ChipGroup, isInTask: Boolean) {
        var addedInTask = isInTask
        val tagName = group.name
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
                if (isInTask) _delFromTask.add(group)
                else _addToTask.remove(group)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInTask = true
                if (!isInTask) _addToTask.add(group)
                else _delFromTask.remove(group)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageGroups() {
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (group in _addToTask) {
                val res = ApiHelper.createGroupAssignedTask(GroupAssignedTask(group.id, Constants.Task.assignedTaskId!!))
                if (res != null) groups.add(group)
                else showSaveToast = true
            }
            for (group in _delFromTask) {
                val res = ApiHelper.deleteGroupAssignedTask(group.id, Constants.Task.assignedTaskId!!)
                if (res != null) groups.remove(group)
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