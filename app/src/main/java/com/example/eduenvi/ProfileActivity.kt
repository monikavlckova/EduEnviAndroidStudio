package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.databinding.ActivityProfileBinding
import com.example.eduenvi.models.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val IMAGE_REQUEST_CODE = 100
    private var changedImage = false
    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val teacher = Constants.Teacher
        setValuesInEditPanel()

        binding.firstNameLastName.text = "${teacher.name} ${teacher.lastName}"

        if (teacher.imageId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val image = ApiHelper.getImage(teacher.imageId!!)
                if (image != null) {
                    withContext(Dispatchers.Main) {
                        Constants.imageManager.setImage(
                            image.url,
                            myContext,
                            binding.profileImage
                        )
                    }
                }
            }
        }

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
                    if (changedImage) {
                        val image = ApiHelper.createImage(Image(0, ""))//TODO
                        if (image != null) teacher.imageId = image.id
                    }
                    val result = ApiHelper.updateTeacher(teacher.id, teacher)
                    withContext(Dispatchers.Main) {
                        if (result == null)
                            Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                    }
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

        binding.changeProfileImage.setOnClickListener {
            pickImageGallery()//TODO zmenit
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
                    val result = ApiHelper.updateTeacher(teacher.id, teacher)
                    withContext(Dispatchers.Main) {
                        if (result == null)
                            Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                    }
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

        binding.editFirstName.addTextChangedListener {
            binding.editFirstNameTextInputLayout.error = null
        }
        binding.editLastName.addTextChangedListener {
            binding.editLastNameTextInputLayout.error = null
        }
        binding.editUserName.addTextChangedListener {
            binding.editUserNameTextInputLayout.error = null
        }
        binding.editEmail.addTextChangedListener { binding.editEmailTextInputLayout.error = null }
        binding.password1.addTextChangedListener { binding.password1TextInputLayout.error = null }
        binding.password2.addTextChangedListener { binding.password2TextInputLayout.error = null }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            binding.editProfileImage.setImageURI(data?.data)
            changedImage = true
        }
    }

    private fun closePasswordPanel() {
        binding.password1.text = null
        binding.password2.text = null
        binding.passwordPanel.visibility = View.GONE
    }

    private fun setValuesInEditPanel() {
        val teacher = Constants.Teacher
        binding.editFirstName.setText(teacher.name)
        binding.editLastName.setText(teacher.lastName)
        binding.editUserName.setText(teacher.userName)
        binding.editEmail.setText(teacher.email)
        if (teacher.imageId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val image = ApiHelper.getImage(teacher.imageId!!)
                if (image != null) {
                    withContext(Dispatchers.Main) {
                        Constants.imageManager.setImage(
                            image.url,
                            this@ProfileActivity,
                            binding.editProfileImage
                        )
                    }
                }
            }
        }
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
                    if (teacher != null && teacher.id != Constants.Teacher.id) {
                        binding.editUserNameTextInputLayout.error =
                            Constants.WrongUserNameAlreadyExistMessage
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
                    if (teacher != null && teacher.id != Constants.Teacher.id) {
                        binding.editEmailTextInputLayout.error =
                            Constants.WrongEmailAlreadyExistMessage
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