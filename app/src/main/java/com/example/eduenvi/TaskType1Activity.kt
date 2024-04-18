package com.example.eduenvi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityTaskType1Binding

class TaskType1Activity : AppCompatActivity() {

    lateinit var binding: ActivityTaskType1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskType1Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}