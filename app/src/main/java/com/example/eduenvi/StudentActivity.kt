package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.StudentGroupTasksAdapter
import com.example.eduenvi.adapters.StudentSoloTasksAdapter
import com.example.eduenvi.adapters.StudentTerminatedTasksAdapter
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
    private var soloTasks: List<Task>? = null
    private var groupTasks: List<Task>? = null
    private var terminatedTasks: List<Task>? = null
    private lateinit var soloTasksAdapter: StudentSoloTasksAdapter
    private lateinit var groupTasksAdapter: StudentGroupTasksAdapter
    private lateinit var terminatedTasksAdapter: StudentTerminatedTasksAdapter

    private val myContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.imageManager.setImage(Constants.Student.imageId, myContext, binding.profileImage)
        if (Constants.Student.imageId == null) binding.imageLayout.visibility = View.GONE
        binding.studentName.text = "Vitaj ${Constants.Student.firstName}!"

        setSoloStudentTasksLayout()
        setGroupTasksLayout()
        setTerminatedTasksLayout()

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.ratingButton.setOnClickListener {
            val intent = Intent(this, StudentRatingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setSoloStudentTasksLayout(){
        if (soloTasks == null){
            CoroutineScope(Dispatchers.IO).launch {
                soloTasks = ApiHelper.getStudentsTasks(Constants.Student.id)?.filter { task: Task ->
                    task.deadline == null || task.deadline!! > Calendar.getInstance().time
                }

                withContext(Dispatchers.Main) {
                    if (soloTasks != null) {
                        soloTasksAdapter = StudentSoloTasksAdapter(myContext, soloTasks!!)
                        binding.soloTasksLayout.adapter = soloTasksAdapter
                    }
                }
            }
        }
    }

    private fun setGroupTasksLayout(){
        if (groupTasks == null){
            CoroutineScope(Dispatchers.IO).launch {
                groupTasks = ApiHelper.getTasksInStudentsGroups(Constants.Student.id)

                withContext(Dispatchers.Main) {
                    if (groupTasks != null) {
                        groupTasksAdapter = StudentGroupTasksAdapter(myContext, groupTasks!!)
                        binding.groupTasksLayout.adapter = groupTasksAdapter
                    }
                }
            }
        }
    }

    private fun setTerminatedTasksLayout(){
        if (terminatedTasks == null){
            CoroutineScope(Dispatchers.IO).launch {
                terminatedTasks = ApiHelper.getStudentsTasks(Constants.Student.id)?.filter { task: Task ->
                    task.deadline != null && task.deadline!! < Calendar.getInstance().time
                }

                withContext(Dispatchers.Main) {
                    if (terminatedTasks != null) {
                        terminatedTasksAdapter = StudentTerminatedTasksAdapter(myContext, terminatedTasks!!)
                        binding.terminatedTasksLayout.adapter = terminatedTasksAdapter
                    }
                }
            }
        }
    }

}