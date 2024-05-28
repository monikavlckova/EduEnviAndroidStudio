package com.example.eduenvi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityLoginBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { submitLoginForm(this) }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.backToLoginButton.setOnClickListener { closeForgottenPasswordPanel() }

        binding.forgottenPasswordPanel.setOnClickListener { closeForgottenPasswordPanel() }

        binding.forgottenPasswordButton.setOnClickListener {
            binding.forgottenPasswordPanel.visibility = View.VISIBLE
        }

        binding.sendButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val teacher = ApiHelper.getTeacherByEmail(binding.email.text.toString())

                withContext(Dispatchers.Main) {
                    if (teacher != null) {
                        val newPassword = generateNewPassword()
                        teacher.password = newPassword
                        ApiHelper.updateTeacher(teacher.id, teacher)
                        Constants.emailSender.sendPassword(
                            teacher.email,
                            teacher.userName,
                            newPassword,
                            myContext
                        )
                        closeForgottenPasswordPanel()
                    } else {
                        binding.emailTextInputLayout.error = Constants.WrongEmailDoesNotExistMessage
                    }
                }
            }
        }

        binding.email.addTextChangedListener {
            binding.emailTextInputLayout.error = null
            binding.passwordTextInputLayout.error = null
        }
        binding.password.addTextChangedListener { binding.passwordTextInputLayout.error = null }
    }

    private fun submitLoginForm(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val teacher = ApiHelper.getTeacherByLogin(
                binding.userName.text.toString(),
                binding.password.text.toString()
            )

            withContext(Dispatchers.Main) {
                if (teacher != null) {
                    Constants.Teacher = teacher
                    resetLoginForm()
                    val intent = Intent(context, ClassroomsActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.passwordTextInputLayout.error = Constants.WrongUserNameOrPasswordMessage
                    binding.password.requestFocus()
                }
            }
        }
    }


    private fun closeForgottenPasswordPanel() {
        binding.forgottenPasswordPanel.visibility = View.GONE
        resetEmailForm()
    }

    private fun generateNewPassword(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(8) { charPool.random() }.joinToString("")
    }

    private fun resetLoginForm() {
        binding.userName.text = null
        binding.password.text = null
        binding.passwordTextInputLayout.error = null
    }

    private fun resetEmailForm() {
        binding.email.text = null
        binding.emailTextInputLayout.error = null
    }
}