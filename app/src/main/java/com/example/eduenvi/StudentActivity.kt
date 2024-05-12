package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.StudentSideTasksAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityStudentBinding
import com.example.eduenvi.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class StudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentBinding
    private var tasks: MutableList<Task>? = null
    private lateinit var tasksAdapter: StudentSideTasksAdapter

    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.imageManager.setImage(Constants.Student.imageId, myContext, binding.profileImage)
        if (Constants.Student.imageId == null) binding.imageLayout.visibility = View.GONE
        binding.studentName.text = "Vitaj ${Constants.Student.firstName}!"

        CoroutineScope(Dispatchers.IO).launch {
            tasks = getSoloStudentTasks()
            val groupTasks = getGroupTasks()
            if (groupTasks != null) tasks?.addAll(groupTasks)
            withContext(Dispatchers.Main) {
                if (tasks != null) {
                    tasksAdapter = StudentSideTasksAdapter(myContext, tasks!!)
                    binding.tasksLayout.adapter = tasksAdapter
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.ratingButton.setOnClickListener {
            val intent = Intent(this, StudentRatingActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun getSoloStudentTasks(): MutableList<Task>? {
        return ApiHelper.getStudentsTasks(Constants.Student.id)
            ?.map { task: Task ->
                task.isSolo = true
                if (task.deadline != null && task.deadline!! <= Calendar.getInstance().time) {
                    task.isTerminated = true
                }
                task
            } as MutableList<Task>?
    }

    private suspend fun getGroupTasks(): Collection<Task>? {
        return ApiHelper.getTasksInStudentsGroups(Constants.Student.id)
            ?.map { task: Task ->
                task.isGroup = true
                if (task.deadline != null && task.deadline!! <= Calendar.getInstance().time) {
                    task.isTerminated = true
                }
                task
            }
    }


}