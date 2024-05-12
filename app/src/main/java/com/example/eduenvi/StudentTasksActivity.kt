package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.StudentTasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityStudentTasksBinding
import com.example.eduenvi.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityStudentTasksBinding
    private val myContext = this
    private var tasks = mutableListOf<Task>()
    private lateinit var adapter: StudentTasksAdapter
    private val assignTaskFragment = AssignTaskFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAssignTaskFragment(savedInstanceState)
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
                val res = ApiHelper.deleteStudentAssignedTask(
                    Constants.Student.id,
                    Constants.Task.assignedTaskId!!
                )

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
            assignTaskFragment.load()
            assignTaskFragment.setTaskPanelCreatingNew()
            binding.assignTaskFragmentLayout.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            val saveSuccessful = assignTaskFragment.save()
            if (saveSuccessful) {
                closeAssignTaskFragmentLayout()
                loadTasksToLayout()
            }
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
            val t = ApiHelper.getStudentsTasks(Constants.Student.id)
            tasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = StudentTasksAdapter(myContext, tasks)
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