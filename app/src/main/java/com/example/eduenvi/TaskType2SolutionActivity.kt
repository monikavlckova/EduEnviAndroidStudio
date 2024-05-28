package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityTaskType2SolutionBinding

class TaskType2SolutionActivity : AppCompatActivity() {
    lateinit var binding: ActivityTaskType2SolutionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType2SolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, TaskAssignedStudentsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            finish()
            startActivity(intent)
        }
    }
}