package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val myContext = this
    private var imageId: Int? = Constants.Teacher.imageId
    private lateinit var viewModel: MyViewModel
    private val fragment = ImageGalleryFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val teacher = Constants.Teacher

        loadGalleryFragment(savedInstanceState)
        loadViewModel()

        binding.firstNameLastName.text = "${teacher.firstName} ${teacher.lastName}"
        Constants.imageManager.setImage(teacher.imageId, myContext, binding.profileImage)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomsActivity::class.java)
            startActivity(intent)
        }

        binding.saveButton.setOnClickListener {
            val validName = validFirstName()
            val validLastName = validLastName()
            val validUserName = validUserName()
            val validEmail = validEmail()
            if (validName && validLastName && validUserName && validEmail) {
                teacher.firstName = binding.editFirstName.text.toString()
                teacher.lastName = binding.editLastName.text.toString()
                teacher.userName = binding.editUserName.text.toString()
                teacher.email = binding.editEmail.text.toString()
                teacher.imageId = imageId
                CoroutineScope(Dispatchers.IO).launch {
                    val res = ApiHelper.updateTeacher(teacher.id, teacher)
                    withContext(Dispatchers.Main) {
                        if (res == null) {
                            Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                        } else {
                            Constants.Teacher = res
                        }
                        val intent = Intent(myContext, ProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        binding.editButton.setOnClickListener {
            openEditPanel()
        }

        binding.editProfileImage.setOnClickListener {
            fragment.load()
            binding.fragmentLayout.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
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
                    val res = ApiHelper.updateTeacher(teacher.id, teacher)
                    withContext(Dispatchers.Main) {
                        if (res == null) {
                            Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                closePasswordPanel()
            }
        }

        binding.closeEditPanel.setOnClickListener {
            binding.mainPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            binding.editPanel.visibility = View.VISIBLE
            //supportFragmentManager.popBackStack()
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

    private fun loadGalleryFragment(savedInstanceState: Bundle?){
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    private fun loadViewModel(){
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(this) { image ->
            imageId = image.id
            openEditPanel()
        }
    }
    private fun openEditPanel() {
        setValuesInEditPanel()
        binding.fragmentLayout.visibility = View.GONE
        binding.mainPanel.visibility = View.GONE
        binding.editPanel.visibility = View.VISIBLE
    }

    private fun closePasswordPanel() {
        binding.password1.text = null
        binding.password2.text = null
        binding.passwordPanel.visibility = View.GONE
    }

    private fun setValuesInEditPanel() {
        val teacher = Constants.Teacher
        binding.editFirstName.setText(teacher.firstName)
        binding.editLastName.setText(teacher.lastName)
        binding.editUserName.setText(teacher.userName)
        binding.editEmail.setText(teacher.email)
        Constants.imageManager.setImage(imageId, this, binding.profileImageEditPanel)
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
        }
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
        return isValid
    }

    private fun validEmail(): Boolean {
        var isValid = true
        val email = binding.editEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmailTextInputLayout.error = Constants.WrongEmailFormatMessage
            return false
        }
        CoroutineScope(Dispatchers.IO).launch {
            val teacher = ApiHelper.getTeacherByEmail(email)

            withContext(Dispatchers.Main) {
                if (teacher != null && teacher.id != Constants.Teacher.id) {
                    binding.editEmailTextInputLayout.error = Constants.WrongEmailAlreadyExistMessage
                    isValid = false
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