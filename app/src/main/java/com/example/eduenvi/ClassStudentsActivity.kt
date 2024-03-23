package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.ClassroomStudentsAdapter
import com.example.eduenvi.databinding.ActivityClassStudentsBinding
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassStudentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassStudentsBinding
    private var _creatingNew: Boolean? = null
    private var students: MutableList<Student>? = null
    private lateinit var adapter: ClassroomStudentsAdapter
    private var changeToClassroom = Constants.Classroom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myContext = this
        CoroutineScope(Dispatchers.IO).launch {
            students =
                ApiHelper.getStudentsInClassroom(Constants.Classroom.id) as MutableList<Student>?

            withContext(Dispatchers.Main) {
                if (students != null) {
                    adapter = ClassroomStudentsAdapter(myContext, students!!)
                    binding.studentsLayout.adapter = adapter
                }
            }
        }
        val classroom = Constants.Classroom
        binding.className.text = classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }
        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, ClassTasksActivity::class.java)
            startActivity(intent)
        }
        binding.groupsButton.setOnClickListener {
            val intent = Intent(this, ClassGroupsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            binding.firstName.text = null
            binding.lastName.text = null
            binding.loginCode.text = null
            binding.classroomTextInputLayout.visibility = View.GONE
            binding.saveButton.text = Constants.SaveButtonTextCreate
            binding.studentPanel.visibility = View.VISIBLE
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.firstName.setText(Constants.Student.name)
            binding.lastName.setText(Constants.Student.lastName)
            binding.loginCode.setText(Constants.Student.loginCode)
            binding.classroom.setText(Constants.Classroom.name)
            binding.classroomTextInputLayout.visibility = View.VISIBLE
            binding.studentPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            setClassroomsDropdown()
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
                        null
                    )
                var res: Student?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNew == false) {
                        student.id = Constants.Student.id
                        res = ApiHelper.updateStudent(student.id, student)
                        students!!.remove(Constants.Student)
                    } else {
                        res = ApiHelper.createStudent(student)
                    }
                    Constants.Student = student
                    students!!.add(student)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataChenged()
                    }
                }
                closeStudentPanel()
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteStudentString(Constants.Student)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.deleteStudent(Constants.Student.id)
                students!!.remove(Constants.Student)
                withContext(Dispatchers.Main) {
                    adapter.notifyDataChenged()
                }
            }
            val intent = Intent(this, ClassStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.generateLoginCode.setOnClickListener { binding.loginCode.setText(generateLoginCode())}

        binding.closeStudentPanel.setOnClickListener { closeStudentPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun closeStudentPanel() {
        binding.studentPanel.visibility = View.GONE
        binding.firstNameTextInputLayout.error = null
        binding.firstName.text = null
        binding.lastNameTextInputLayout.error = null
        binding.lastName.text = null
        binding.loginCodeTextInputLayout.error = null
        binding.loginCode.text = null
    }

    private fun setClassroomsDropdown() {
        val myContext = this
        CoroutineScope(Dispatchers.IO).launch {
            val classrooms = ApiHelper.getTeachersClassrooms(Constants.Teacher.id)
            withContext(Dispatchers.Main) {
                if (classrooms != null) {
                    val listAdapter = ArrayAdapter(myContext,  R.layout.dropdown_list_item, classrooms)
                    binding.classroom.setAdapter(listAdapter)//TODO mozno zmenit a robit to v on create? lebo vzdy su triedy rovnake
                    binding.classroom.setOnItemClickListener { adapterView, _, i, _ ->
                        changeToClassroom = adapterView.getItemAtPosition(i) as Classroom
                    }
                }
            }
        }
    }

    private fun deleteStudentFromGroups(studentId: Int) {
        //TODO
    }

    private fun generateLoginCode() :String{
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
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