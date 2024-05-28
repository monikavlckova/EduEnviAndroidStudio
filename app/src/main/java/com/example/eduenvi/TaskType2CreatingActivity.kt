package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityTaskType2CreatingBinding

class TaskType2CreatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskType2CreatingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType2CreatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            finish()
            startActivity(intent)
        }
    }
}