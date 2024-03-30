package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.GroupTasksAdapter
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
    private var tasks: MutableList<Task>? = null
    private lateinit var adapter: GroupTasksAdapter
    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val group = Constants.Group
        CoroutineScope(Dispatchers.IO).launch {
            tasks = ApiHelper.getGroupsTasks(group.id) as MutableList<Task>?

            withContext(Dispatchers.Main) {
                if (tasks != null) {
                    adapter = GroupTasksAdapter(myContext, tasks!!)
                    binding.tasksLayout.adapter = adapter
                }
            }
        }

        binding.groupName.text = group.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, GroupStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val result = ApiHelper.deleteGroupTask(group.id, Constants.Task.id)
                withContext(Dispatchers.Main) {
                    if (result != null) if (tasks != null) tasks!!.remove(Constants.Task)
                    else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
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
            //val intent = Intent(this, GroupTasksActivity::class.java)
            //startActivity(intent)
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
        if (isInGroup) chip.setCloseIconResource(R.drawable.baseline_close_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_24)
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            if (addedInGroup) {
                chip.setCloseIconResource(R.drawable.baseline_add_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInGroup = false
                if (isInGroup) _delFromGroup.add(task)
                else _addToGroup.remove(task)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInGroup = true
                if (!isInGroup) _addToGroup.add(task)
                else _delFromGroup.remove(task)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            for (task in _addToGroup) {
                val newTask = ApiHelper.createGroupTask(GroupTask(Constants.Group.id, task.id))
                withContext(Dispatchers.Main) {
                    if (newTask != null) if (tasks != null) tasks!!.add(task)
                    else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                }
            }
            for (task in _delFromGroup) {
                val result = ApiHelper.deleteGroupTask(Constants.Group.id, task.id)
                withContext(Dispatchers.Main) {
                    if (result != null) if (tasks != null) tasks!!.remove(task)
                    else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
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