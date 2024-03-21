package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val teacher = Constants.Teacher
        setValuesInEditPanel()

        binding.firstNameLastName.text = "${teacher!!.name} ${teacher.lastName}"

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        binding.saveButton.setOnClickListener {
            val validName = validFirstName()
            val validLastName = validLastName()
            val validUserName = validUserName()
            val validEmail = validEmail()
            if (validName && validLastName && validUserName && validEmail) {
                teacher.name = binding.editFirstName.text.toString()
                teacher.lastName = binding.editLastName.text.toString()
                teacher.userName = binding.editUserName.text.toString()
                teacher.email = binding.editEmail.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    ApiHelper.updateTeacher(teacher.id, teacher)
                }
                binding.mainPanel.visibility = View.VISIBLE
                binding.editPanel.visibility = View.GONE
            }
        }

        binding.editButton.setOnClickListener {
            setValuesInEditPanel()
            binding.mainPanel.visibility = View.GONE
            binding.editPanel.visibility = View.VISIBLE
        }

        binding.changePassword.setOnClickListener {
            binding.passwordPanel.visibility = View.VISIBLE
        }

        binding.editChangePassword.setOnClickListener {
            binding.passwordPanel.visibility = View.VISIBLE
        }

        binding.savePasswordButton.setOnClickListener {
            if (isValidPassword()) {
                teacher.password = binding.password1.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    ApiHelper.updateTeacher(teacher.id, teacher)
                }
                closePasswordPanel()
            }
        }

        binding.closeEditPanel.setOnClickListener {
            binding.mainPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.passwordPanel.setOnClickListener { closePasswordPanel() }
        binding.closePasswordPanel.setOnClickListener { closePasswordPanel() }
    }

    private fun closePasswordPanel() {
        binding.password1.text = null
        binding.password2.text = null
        binding.passwordPanel.visibility = View.GONE
    }

    private fun setValuesInEditPanel() {
        val teacher = Constants.Teacher!!
        binding.editFirstName.setText(teacher.name)
        binding.editLastName.setText(teacher.lastName)
        binding.editUserName.setText(teacher.userName)
        binding.editEmail.setText(teacher.email)
    }

    private fun validFirstName(): Boolean {
        if (binding.editFirstName.text.toString().length < Constants.MinimalFirstNameLength) {
            binding.editFirstNameTextInputLayout.error = Constants.WrongFirstNameFormatMessage
            return false
        }
        return true
    }

    private fun validLastName(): Boolean {
        if (binding.editLastName.text.toString().length < Constants.MinimalLastNameLength) {
            binding.editLastNameTextInputLayout.error = Constants.WrongLastNameFormatMessage
            return false
        }
        return true
    }

    private fun validUserName(): Boolean {
        var isValid = true
        val userName = binding.editUserName.text.toString()
        if (userName.length < Constants.MinimalUserNameLength) {
            binding.editUserNameTextInputLayout.error = Constants.WrongUserNameFormatMessage
            return false
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val teacher = ApiHelper.getTeacherByUserName(userName)

                withContext(Dispatchers.Main) {
                    if (teacher != null && teacher.id != Constants.Teacher!!.id) {
                        binding.editUserNameTextInputLayout.error = Constants.WrongUserNameAlreadyExistMessage
                        isValid = false
                    }
                }
            }
        }
        return isValid
    }

    private fun validEmail(): Boolean {
        var isValid = true
        val email = binding.editEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmailTextInputLayout.error = Constants.WrongEmailFormatMessage
            return false
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val teacher = ApiHelper.getTeacherByEmail(email)

                withContext(Dispatchers.Main) {
                    if (teacher != null && teacher.id != Constants.Teacher!!.id) {
                        binding.editEmailTextInputLayout.error = Constants.WrongEmailAlreadyExistMessage
                        isValid = false
                    }
                }
            }
        }
        return isValid
    }

    private fun isValidPassword(): Boolean {
        var valid = true

        if (binding.password1.text.toString() != binding.password2.text.toString()) {
            binding.password2TextInputLayout.error = Constants.WrongPasswordsNotSameMessage
            valid = false
        }

        if (binding.password1.text.toString().length < Constants.MinimalPasswordLength) {
            binding.password1TextInputLayout.error = Constants.WrongPasswordFormatMessage
            valid = false
        }

        return valid
    }
}