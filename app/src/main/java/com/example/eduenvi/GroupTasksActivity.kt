package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.GroupTasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityGroupTasksBinding
import com.example.eduenvi.models.GroupTask
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityGroupTasksBinding

    private var _delFromGroup: HashSet<Task> = HashSet()
    private var _addToGroup: HashSet<Task> = HashSet()
    private var tasks = mutableListOf<Task>()
    private lateinit var adapter: GroupTasksAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTasksToLayout()

        binding.groupName.text = Constants.Group.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, GroupStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteGroupTask(Constants.Group.id, Constants.Task.id)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        tasks.remove(Constants.Task)
                        adapter.notifyDataChanged()
                    } else {
                        Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                    }
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.addButton.setOnClickListener { setActiveTasksPanel() }

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
            val t = ApiHelper.getGroupsTasks(Constants.Group.id)
            tasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = GroupTasksAdapter(myContext, tasks)
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
            val tasksInGroup = ApiHelper.getGroupsTasks(Constants.Group.id)
            val tasksNotInGroup = ApiHelper.getTasksFromTeacherNotInGroup(
                Constants.Teacher.id,
                Constants.Group.id
            )

            withContext(Dispatchers.Main) {
                addTasksToLists(tasksInGroup, tasksNotInGroup)
            }
        }
    }

    private fun addTasksToLists(tasksInGroup: List<Task>?, tasksNotInGroup: List<Task>?) {
        tasksInGroup?.forEach { task ->
            addTaskToList(task, binding.chipGroupIn, true)
        }
        tasksNotInGroup?.forEach { task ->
            addTaskToList(task, binding.chipGroupNotIn, false)
        }
    }

    private fun addTaskToList(task: Task, chipGroup: ChipGroup, isInGroup: Boolean) {
        var addedInGroup = isInGroup
        val tagName = task.name
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
            if (addedInGroup) {
                chip.setCloseIconResource(R.drawable.baseline_add_on_primary_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInGroup = false
                if (isInGroup) {
                    _delFromGroup.add(task)
                } else {
                    _addToGroup.remove(task)
                }
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_on_background_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInGroup = true
                if (!isInGroup) {
                    _addToGroup.add(task)
                } else {
                    _delFromGroup.remove(task)
                }
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageTasks() {
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (task in _addToGroup) {
                val res = ApiHelper.createGroupTask(GroupTask(Constants.Group.id, task.id))
                if (res != null) tasks.add(task)
                else showSaveToast = true
            }
            for (task in _delFromGroup) {
                val res = ApiHelper.deleteGroupTask(Constants.Group.id, task.id)
                if (res != null) tasks.remove(task)
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