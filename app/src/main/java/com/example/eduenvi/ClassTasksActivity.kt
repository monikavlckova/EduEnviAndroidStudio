package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.ClassroomTasksAdapter
import com.example.eduenvi.databinding.ActivityClassTasksBinding
import com.example.eduenvi.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClassTasksActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassTasksBinding
    private var _creatingNew: Boolean? = null
    private var tasks :MutableList<Task>? = null
    private lateinit var adapter: ClassroomTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myContext = this
        CoroutineScope(Dispatchers.IO).launch {
            tasks = ApiHelper.getTasksInClassroom(Constants.Classroom.id) as MutableList<Task>?

            withContext(Dispatchers.Main) {
                if (tasks != null) {
                    adapter = ClassroomTasksAdapter(myContext, tasks!!)
                    binding.tasksLayout.adapter = adapter
                }
            }
        }
        val classroom = Constants.Classroom
        binding.className.text = classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }
        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, ClassGroupsActivity::class.java)
            startActivity(intent)
        }
        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, ClassStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            //TODO zobraz panel na vytvaranie a upravu ulohy
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            binding.editPanel.visibility = View.GONE
            //TODO zobraz panel na vytvaranie a upravu ulohy
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteGroupString(Constants.Group)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.deleteTask(Constants.Task.id)
                tasks!!.remove(Constants.Task)
                withContext(Dispatchers.Main) {
                    adapter.notifyDataChenged()
                }
            }
            val intent = Intent(this, ClassTasksActivity::class.java)
            startActivity(intent)
        }

        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }
}