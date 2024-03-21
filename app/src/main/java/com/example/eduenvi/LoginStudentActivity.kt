package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityLoginStudentBinding
import com.example.eduenvi.models.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginStudentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val context = this
            CoroutineScope(Dispatchers.IO).launch {
                val student = ApiHelper.getStudentByLoginCode(binding.loginCode.text.toString())

                withContext(Dispatchers.Main) {
                    if (validLoginCode(student)) {
                        Constants.Student = student!!
                        val intent = Intent(context, StudentTasksActivity::class.java) //TODO zmen StudentTaskActivity
                        startActivity(intent)
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }
    }

    private fun validLoginCode(student: Student?): Boolean {
        if (student == null) {
            binding.loginCodeTextInputLayout.error = Constants.WrongLoginCodeMessage
            return false
        }

        return true
    }
}