package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.ClassroomTasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityClassroomTasksBinding
import com.example.eduenvi.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassroomTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomTasksBinding
    private val myContext = this
    private var classroomTasks = mutableListOf<Task>()
    private lateinit var adapter: ClassroomTasksAdapter
    private val assignTaskFragment = AssignTaskFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAssignTaskFragment(savedInstanceState)
        loadTasksToLayout()

        binding.classroomName.text = Constants.Classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, ClassroomGroupsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, ClassroomStudentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            assignTaskFragment.load()
            assignTaskFragment.setTaskPanelCreatingNew()
            binding.assignTaskFragmentLayout.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
        }

        binding.editButton.setOnClickListener {
            assignTaskFragment.load()
            assignTaskFragment.setTaskPanelUpdating()
            binding.assignTaskFragmentLayout.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
            binding.editPanel.visibility = View.GONE
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

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteTaskFromClassroomString(Constants.Task)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteAssignedTask(Constants.Task.assignedTaskId!!)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        classroomTasks.remove(Constants.Task)
                        adapter.notifyDataChanged()
                    } else {
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    }
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.closeAssignTaskFragmentButton.setOnClickListener { closeAssignTaskFragmentLayout() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun loadTasksToLayout() {
        CoroutineScope(Dispatchers.IO).launch {
            val t = ApiHelper.getTasksInClassroom(Constants.Classroom.id)
            classroomTasks = if (t == null) mutableListOf() else t as MutableList<Task>

            withContext(Dispatchers.Main) {
                adapter = ClassroomTasksAdapter(myContext, classroomTasks)
                binding.tasksLayout.adapter = adapter
            }
        }
    }

    private fun loadAssignTaskFragment(savedInstanceState: Bundle?) {
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