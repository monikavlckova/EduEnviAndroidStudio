package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.GroupTasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityGroupTasksBinding
import com.example.eduenvi.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityGroupTasksBinding
    private val myContext = this
    private var tasks = mutableListOf<Task>()
    private lateinit var adapter: GroupTasksAdapter
    private val assignTaskFragment = AssignTaskFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAssignTaskFragment(savedInstanceState)
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
                val res = ApiHelper.deleteGroupAssignedTask(
                    Constants.Group.id,
                    Constants.Task.assignedTaskId!!
                )
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

        binding.addButton.setOnClickListener {
            assignTaskFragment.load()
            assignTaskFragment.setTaskPanelCreatingNew()
            binding.assignTaskFragmentLayout.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {//TODO znovu nacitaj loadTasksToLayout
            val saveSuccessful = assignTaskFragment.save()
            if (saveSuccessful) closeAssignTaskFragmentLayout()
        }

        binding.createNewTask.setOnClickListener {
            //TODO
        }

        binding.closeAssignTaskFragmentButton.setOnClickListener { closeAssignTaskFragmentLayout() }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun loadTasksToLayout() {
        CoroutineScope(Dispatchers.IO).launch {
            val t = ApiHelper.getGroupsTasks(Constants.Group.id)
            tasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = GroupTasksAdapter(myContext, tasks)
                binding.tasksLayout.adapter = adapter
            }
        }
    }

    private fun loadAssignTaskFragment(savedInstanceState: Bundle?){
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.assignTaskFragmentContainer, assignTaskFragment)
                .commit()
        }
    }

    private fun closeAssignTaskFragmentLayout() {
        binding.assignTaskFragmentLayout.visibility = View.GONE
        binding.mainPanel.visibility = View.VISIBLE
        //supportFragmentManager.popBackStack()
    }
}