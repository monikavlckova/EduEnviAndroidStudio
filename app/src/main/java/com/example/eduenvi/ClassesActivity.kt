package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.adapters.ClassroomAdapter
import com.example.eduenvi.databinding.ActivityClassesBinding
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.StudentTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//TODO pridet obrazok do classpanel co je zaroven aj edit, ak edit nacitat ho ak ho ma
class ClassesActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassesBinding
    private var _creatingNew: Boolean? = null
    private var classes: MutableList<Classroom>? = null
    private lateinit var adapter: ClassroomAdapter
    private val myContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            classes =
                ApiHelper.getTeachersClassrooms(Constants.Teacher.id) as MutableList<Classroom>?

            withContext(Dispatchers.Main) {
                if (classes != null) {
                    adapter = ClassroomAdapter(myContext, classes!!)
                    binding.classroomLayout.adapter = adapter
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            binding.saveButton.text = Constants.SaveButtonTextCreate
            binding.classPanel.visibility = View.VISIBLE
        }

        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.className.setText(Constants.Classroom.name)
            binding.classPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            if (validClassroomName()) {
                val classroom =
                    Classroom(0, Constants.Teacher.id, binding.className.text.toString(), null)//TODO zmenit null
                if (_creatingNew == false) {
                    classroom.id = Constants.Classroom.id
                    CoroutineScope(Dispatchers.IO).launch {
                        ApiHelper.updateClassroom(classroom.id, classroom)
                        Log.v("DB", "trieda " + Constants.Classroom.name)
                        classes!!.remove(Constants.Classroom)//TODO aj riadok pod, ak sa podaril update else toast nepodarilo sa
                        classes!!.add(classroom)
                        withContext(Dispatchers.Main) {
                            adapter.notifyDataChanged()
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        ApiHelper.createClassroom(classroom)
                        classes!!.add(classroom)//TODO ak sa podaril create else toast nepodarilo sa
                        withContext(Dispatchers.Main) {
                            adapter.notifyDataChanged()
                        }
                    }
                }
                closeClassPanel()
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteClassroomString(Constants.Classroom)
        }

        binding.confirmDelete.setOnClickListener {
            switchClassroomTasksToStudentTasks()
            switchGroupTasksToStudentTasks()
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.deleteClassroom(Constants.Classroom.id)
                classes!!.remove(Constants.Classroom)//TODO ak sa podaril delete else toast nepodarilo sa
                withContext(Dispatchers.Main) {
                    adapter.notifyDataChanged()
                }
            }
            binding.deletePanel.visibility = View.GONE
        }

        binding.closeClassPanel.setOnClickListener { closeClassPanel() }
        binding.classPanel.setOnClickListener { closeClassPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.className.addTextChangedListener { binding.classNameTextInputLayout.error = null }
    }

    private fun closeClassPanel() {
        binding.classPanel.visibility = View.GONE
        binding.classNameTextInputLayout.error =
            null //TODO zmenit na iserrorenabled false? potom to uskakuje, ako chcela som ale chcem?
        binding.className.text = null
    }

    private fun switchClassroomTasksToStudentTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val students = ApiHelper.getStudentsInClassroom(Constants.Classroom.id)
            val classroomTasks = ApiHelper.getTasksInClassroom(Constants.Classroom.id)

            if (students != null && classroomTasks != null)
                for (student in students) for (task in classroomTasks) {
                    val studentTask = StudentTask(student.id, task.id)
                    ApiHelper.createStudentTask(studentTask)
                }
        }
    }

    private fun switchGroupTasksToStudentTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val groups = ApiHelper.getGroupsInClassroom(Constants.Classroom.id)

            if (groups != null)
                for (group in groups) {
                    val studentsInGroup = ApiHelper.getStudentsInGroup(group.id)
                    val groupTasks = ApiHelper.getGroupsTasks(group.id)
                    if (studentsInGroup != null && groupTasks != null)
                        for (student in studentsInGroup) for (task in groupTasks) {
                            val studentTask = StudentTask(student.id, task.id)
                            ApiHelper.createStudentTask(studentTask)
                        }
                }
        }

    }

    private fun validClassroomName(): Boolean {
        if (binding.className.text.toString().length < Constants.MinimalClassroomNameLength) {
            binding.classNameTextInputLayout.error = Constants.WrongClassroomNameFormatMessage
            return false
        }
        return true
    }

}