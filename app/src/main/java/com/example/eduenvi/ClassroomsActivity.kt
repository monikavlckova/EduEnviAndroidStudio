package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.eduenvi.adapters.ClassroomAdapter
import com.example.eduenvi.databinding.ActivityClassroomsBinding
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.StudentTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//TODO ak otvorim  upravit coma obr a potom upravit co nema, obrazok ostane, aj v inych aktivitach
class ClassroomsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomsBinding
    private var _creatingNew: Boolean? = null
    private var classrooms: MutableList<Classroom>? = null
    private lateinit var adapter: ClassroomAdapter
    private val myContext = this
    private var imageId :Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            classrooms =
                ApiHelper.getTeachersClassrooms(Constants.Teacher.id) as MutableList<Classroom>?

            withContext(Dispatchers.Main) {
                if (classrooms != null) {
                    adapter = ClassroomAdapter(myContext, classrooms!!)
                    binding.classroomLayout.adapter = adapter
                }
            }
        }

        binding.menuButton.setOnClickListener { binding.menuPanel.visibility = View.VISIBLE }

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            binding.saveButton.text = Constants.SaveButtonTextCreate
            binding.classroomPanel.visibility = View.VISIBLE
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            imageId = Constants.Classroom.imageId
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.classroomName.setText(Constants.Classroom.name)
            Constants.imageManager.setImage(Constants.Classroom.imageId, this, binding.ClassroomImage)
            binding.classroomPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.editClassroomImage.setOnClickListener {
            //TODO otvor panel na vyberanie obrazkov, tam bude nejake tlacidlo uloz a v nom spravit ak nove vytvor a nastav imageId na nove ak vybera uz z db tak tiez nastav imageId
        }

        binding.saveButton.setOnClickListener {
            if (validClassroomName()) {
                val classroom =
                    Classroom(
                        0,
                        Constants.Teacher.id,
                        binding.classroomName.text.toString(),
                        imageId
                    )
                if (_creatingNew == false) {
                    classroom.id = Constants.Classroom.id
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = ApiHelper.updateClassroom(classroom.id, classroom)
                        withContext(Dispatchers.Main) {
                            if (result != null) {
                                if (classrooms != null) {
                                    classrooms!!.remove(Constants.Classroom)
                                    classrooms!!.add(classroom)
                                }
                            } else Toast.makeText(
                                myContext,
                                Constants.SaveError,
                                Toast.LENGTH_LONG
                            ).show()
                            adapter.notifyDataChanged()
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = ApiHelper.createClassroom(classroom)
                        withContext(Dispatchers.Main) {
                            if (result != null) if (classrooms != null) classrooms!!.add(classroom)
                            else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG)
                                .show()
                            adapter.notifyDataChanged()
                        }
                    }
                }
                closeClassroomPanel()
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
                val result = ApiHelper.deleteClassroom(Constants.Classroom.id)
                withContext(Dispatchers.Main) {
                    if (result != null) if (classrooms != null) classrooms!!.remove(Constants.Classroom)
                    else Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    adapter.notifyDataChanged()
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.menuPanel.setOnClickListener { binding.menuPanel.visibility = View.GONE }
        binding.closeClassroomPanel.setOnClickListener { closeClassroomPanel() }
        binding.classroomPanel.setOnClickListener { closeClassroomPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.classroomName.addTextChangedListener { binding.classroomNameTextInputLayout.error = null }
    }

    private fun closeClassroomPanel() {
        binding.classroomPanel.visibility = View.GONE
        binding.classroomNameTextInputLayout.error = null
        binding.classroomName.text = null
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
        if (binding.classroomName.text.toString().length < Constants.MinimalClassroomNameLength) {
            binding.classroomNameTextInputLayout.error = Constants.WrongClassroomNameFormatMessage
            return false
        }
        return true
    }

}