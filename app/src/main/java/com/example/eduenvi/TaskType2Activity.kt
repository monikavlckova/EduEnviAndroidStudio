package com.example.eduenvi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityTaskType2Binding

lateinit var binding: ActivityTaskType2Binding
class TaskType2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}