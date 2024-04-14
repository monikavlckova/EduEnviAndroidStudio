package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.ClassroomStudentsAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityClassroomStudentsBinding
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassroomStudentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomStudentsBinding
    private var _creatingNew: Boolean? = null
    private var students: MutableList<Student>? = null
    private lateinit var adapter: ClassroomStudentsAdapter
    private var changeToClassroom = Constants.Classroom
    private val myContext = this
    private var imageId: Int? = null
    private lateinit var viewModel: MyViewModel
    private var teachersClassrooms: List<Classroom>? = null
    private val fragment = ImageGalleryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(this) { image ->
            imageId = image.id
            Constants.imageManager.setImage(image.url, this, binding.studentImage)
            binding.fragmentLayout.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.IO).launch {
            students =
                ApiHelper.getStudentsInClassroom(Constants.Classroom.id) as MutableList<Student>?
            teachersClassrooms = ApiHelper.getTeachersClassrooms(Constants.Teacher.id)

            withContext(Dispatchers.Main) {
                if (students != null) {
                    adapter = ClassroomStudentsAdapter(myContext, students!!)
                    binding.studentsLayout.adapter = adapter
                }
            }
        }
        val classroom = Constants.Classroom
        binding.classroomName.text = classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassroomsActivity::class.java)
            startActivity(intent)
        }
        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, ClassroomTasksActivity::class.java)
            startActivity(intent)
        }
        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, ClassroomGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            imageId = null
            Constants.imageManager.setImage("", this, binding.studentImage)
            binding.firstName.text = null
            binding.lastName.text = null
            binding.loginCode.text = null
            binding.classroomTextInputLayout.visibility = View.GONE
            binding.saveButton.text = Constants.SaveButtonTextCreate
            binding.studentPanel.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.GONE
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            imageId = Constants.Student.imageId
            Constants.imageManager.setImage(
                Constants.Student.imageId,
                myContext,
                binding.studentImage
            )
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.firstName.setText(Constants.Student.firstName)
            binding.lastName.setText(Constants.Student.lastName)
            binding.loginCode.setText(Constants.Student.loginCode)
            binding.classroom.setText(Constants.Classroom.name)
            binding.classroomTextInputLayout.visibility = View.VISIBLE
            binding.studentPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.mainPanel.visibility = View.GONE
            setClassroomsDropdown()
        }

        binding.editStudentImage.setOnClickListener {
            fragment.load()
            binding.fragmentLayout.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            val validName = validName()
            val validLastName = validLastName()
            val validLoginCode = validLoginCode()
            if (validName && validLastName && validLoginCode) {
                val student =
                    Student(
                        0,
                        changeToClassroom.id,
                        binding.firstName.text.toString(),
                        binding.lastName.text.toString(),
                        binding.loginCode.text.toString(),
                        imageId
                    )
                var res: Student?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNew == false) {
                        student.id = Constants.Student.id
                        if (changeToClassroom.id != Constants.Classroom.id) deleteStudentFromGroups(
                            student.id
                        )
                        res = ApiHelper.updateStudent(student.id, student)
                        if (res != null)
                            if (students != null) students!!.remove(Constants.Student)
                    } else res = ApiHelper.createStudent(student)

                    withContext(Dispatchers.Main) {
                        if (res == null)
                            Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                        else {
                            //Constants.Student = res!!
                            if (changeToClassroom == Constants.Classroom)
                                if (students != null) students!!.add(res!!)
                        }
                        adapter.notifyDataChanged()
                        closeStudentPanel()
                    }
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteStudentString(Constants.Student)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteStudent(Constants.Student.id)
                withContext(Dispatchers.Main) {
                    if (res != null)
                        if (students != null) students!!.remove(Constants.Student)
                        else Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG)
                            .show()
                    adapter.notifyDataChanged()
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            binding.editPanel.visibility = View.VISIBLE
            //supportFragmentManager.popBackStack()
        }

        binding.generateLoginCode.setOnClickListener { binding.loginCode.setText(generateLoginCode()) }

        binding.closeStudentPanel.setOnClickListener { closeStudentPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.firstName.addTextChangedListener { binding.firstNameTextInputLayout.error = null }
        binding.lastName.addTextChangedListener { binding.lastNameTextInputLayout.error = null }
        binding.loginCode.addTextChangedListener { binding.loginCode.error = null }
    }

    private fun closeStudentPanel() {
        binding.studentPanel.visibility = View.GONE
        binding.mainPanel.visibility = View.VISIBLE
        binding.firstNameTextInputLayout.error = null
        binding.firstName.text = null
        binding.lastNameTextInputLayout.error = null
        binding.lastName.text = null
        binding.loginCodeTextInputLayout.error = null
        binding.loginCode.text = null
        changeToClassroom = Constants.Classroom
    }

    private fun setClassroomsDropdown() {
        if (teachersClassrooms != null) {
            val listAdapter =
                ArrayAdapter(myContext, R.layout.dropdown_list_item, teachersClassrooms!!)
            binding.classroom.setAdapter(listAdapter)
            binding.classroom.setOnItemClickListener { adapterView, _, i, _ ->
                changeToClassroom = adapterView.getItemAtPosition(i) as Classroom
            }
        }
    }

    private fun deleteStudentFromGroups(studentId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val groups = ApiHelper.getStudentsGroups(studentId)
            groups?.forEach { group -> ApiHelper.deleteStudentGroup(studentId, group.id) }
        }
    }

    private fun generateLoginCode(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(8) { charPool.random() }.joinToString("")
    }

    private fun validName(): Boolean {
        if (binding.firstName.text.toString().length < Constants.MinimalFirstNameLength) {
            binding.firstNameTextInputLayout.error = Constants.WrongFirstNameFormatMessage
            return false
        }

        return true
    }

    private fun validLastName(): Boolean {
        if (binding.lastName.text.toString().length < Constants.MinimalLastNameLength) {
            binding.lastNameTextInputLayout.error = Constants.WrongLastNameFormatMessage
            return false
        }

        return true
    }

    private fun validLoginCode(): Boolean {
        var isValid = true
        if (binding.loginCode.text.toString().length < Constants.MinimalLoginCodeLength) {
            binding.loginCodeTextInputLayout.error = Constants.WrongLoginCodeFormatMessage
            return false
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val user = ApiHelper.getStudentByLoginCode(binding.loginCode.text.toString())
                withContext(Dispatchers.Main) {
                    if (user != null && user.id != Constants.Student.id) {
                        binding.loginCodeTextInputLayout.error =
                            Constants.WrongLoginCodeAlreadyExistMessage
                        isValid = false
                    }
                }
            }
            return isValid
        }
    }
}