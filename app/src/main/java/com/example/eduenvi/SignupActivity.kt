package com.example.eduenvi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.databinding.ActivitySignupBinding
import com.example.eduenvi.models.Teacher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener { submitForm(this) }

        binding.name.addTextChangedListener { binding.nameTextInputLayout.error = null }
        binding.lastName.addTextChangedListener { binding.lastNameTextInputLayout.error = null }
        binding.userName.addTextChangedListener { binding.userNameTextInputLayout.error = null }
        binding.email.addTextChangedListener { binding.emailTextInputLayout.error = null }
        binding.password.addTextChangedListener { binding.passwordTextInputLayout.error = null }
    }

    private fun submitForm(context: Context) {
        val validName = validName()
        val validLastName = validLastName()
        val validUserName = validUserName()
        val validPassword = validPassword()
        val validEmail = validEmail()
        if (validName && validLastName && validUserName && validPassword && validEmail) {
            val teacher = Teacher(
                0,
                binding.email.text.toString(),
                binding.name.text.toString(),
                binding.lastName.text.toString(),
                binding.userName.text.toString(),
                binding.password.text.toString(),
                null
            )
            CoroutineScope(Dispatchers.IO).launch {
                val newTeacher = ApiHelper.createTeacher(teacher)

                withContext(Dispatchers.Main) {
                    if (newTeacher != null) {
                        Constants.Teacher = newTeacher
                        resetForm()
                        Constants.emailSender.sendWelcome(newTeacher.email, newTeacher.userName)
                        val intent = Intent(context, ClassesActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun validName(): Boolean {
        val name = binding.name.text.toString()
        if (name.length < Constants.MinimalFirstNameLength) {
            binding.nameTextInputLayout.error = Constants.WrongFirstNameFormatMessage
            return false
        }
        return true
    }

    private fun validLastName(): Boolean {
        val lastName = binding.lastName.text.toString()
        if (lastName.length < Constants.MinimalLastNameLength) {
            binding.lastNameTextInputLayout.error = Constants.WrongLastNameFormatMessage
            return false
        }
        return true
    }

    private fun validUserName(): Boolean {
        var isValid = true
        val userName = binding.userName.text.toString()
        if (userName.length < Constants.MinimalUserNameLength) {
            binding.userNameTextInputLayout.error = Constants.WrongUserNameFormatMessage
            return false
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val teacher = ApiHelper.getTeacherByUserName(userName)

                withContext(Dispatchers.Main) {
                    if (teacher != null) {
                        binding.userNameTextInputLayout.error = Constants.WrongUserNameAlreadyExistMessage
                        isValid = false
                    }
                }
            }
        }
        return isValid
    }

    private fun validEmail(): Boolean {
        var isValid = true
        val email = binding.email.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTextInputLayout.error = Constants.WrongEmailFormatMessage
            return false
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val teacher = ApiHelper.getTeacherByEmail(email)

                withContext(Dispatchers.Main) {
                    if (teacher != null) {
                        binding.emailTextInputLayout.error = Constants.WrongEmailAlreadyExistMessage
                        isValid = false
                    }
                }
            }
        }
        return isValid
    }

    private fun validPassword(): Boolean {
        val password = binding.password.text.toString()
        if (password.length < Constants.MinimalPasswordLength) {
            binding.passwordTextInputLayout.error = Constants.WrongPasswordFormatMessage
            return false
        }
        return true
    }

    private fun resetForm() {
        binding.name.text = null
        binding.lastName.text = null
        binding.userName.text = null
        binding.email.text = null
        binding.password.text = null
        binding.nameTextInputLayout.error = null
        binding.lastNameTextInputLayout.error = null
        binding.userNameTextInputLayout.error = null
        binding.emailTextInputLayout.error = null
        binding.passwordTextInputLayout.error = null
    }

}