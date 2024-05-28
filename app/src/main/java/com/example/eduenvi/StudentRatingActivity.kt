package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityStudentRatingBinding

class StudentRatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentRatingBinding

    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.studentName.text = "Vitaj ${Constants.Student.firstName}!"

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginStudentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, StudentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

    }



}