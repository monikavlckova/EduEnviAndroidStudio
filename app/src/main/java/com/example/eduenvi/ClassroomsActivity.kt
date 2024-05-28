package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.ClassroomAdapter
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityClassroomsBinding
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.Group
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassroomsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomsBinding
    private var _creatingNew: Boolean? = null
    private var classrooms = mutableListOf<Classroom>()
    private lateinit var adapter: ClassroomAdapter
    private val myContext = this
    private var imageId: Int? = null
    private lateinit var viewModel: MyViewModel
    private val fragment = ImageGalleryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadGalleryFragment(savedInstanceState)
        loadViewModel()
        loadClassroomsToLayout()

        binding.menuButton.setOnClickListener {
            binding.menuPanel.visibility =
                if (binding.menuPanel.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.profileButton.setOnClickListener {
            binding.menuPanel.visibility = View.GONE
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNew = true
            imageId = null
            binding.saveButton.text = Constants.SaveButtonTextCreate
            Constants.imageManager.setImage("", this, binding.ClassroomImage)
            binding.classroomPanel.visibility = View.VISIBLE
        }

        binding.editButton.setOnClickListener {
            _creatingNew = false
            imageId = Constants.Classroom.imageId
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.classroomName.setText(Constants.Classroom.name)
            Constants.imageManager.setImage(imageId, this, binding.ClassroomImage)
            binding.classroomPanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.editClassroomImage.setOnClickListener {
            fragment.load()
            binding.fragmentLayout.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            if (validClassroomName()) {
                val teacherId = Constants.Teacher.id
                val name = binding.classroomName.text.toString()
                val classroom = Classroom(0, teacherId, name, imageId)
                var res: Classroom?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNew == false) {
                        classroom.id = Constants.Classroom.id
                        res = ApiHelper.updateClassroom(classroom.id, classroom)
                        if (res != null) {
                            classrooms.remove(Constants.Classroom)
                        }
                    } else {
                        res = ApiHelper.createClassroom(classroom)
                    }
                    withContext(Dispatchers.Main) {
                        if (res != null) {
                            classrooms.add(res!!)
                            adapter.notifyDataChanged()
                        } else {
                            Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
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
            CoroutineScope(Dispatchers.IO).launch {
                deleteGroupsInClassroom()
                deleteStudentsInClassroom()
                val res = ApiHelper.deleteClassroom(Constants.Classroom.id)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        classrooms.remove(Constants.Classroom)
                        adapter.notifyDataChanged()
                    } else {
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    }
                    binding.deletePanel.visibility = View.GONE
                }
            }
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            //supportFragmentManager.popBackStack()
        }

        binding.menuPanel.setOnClickListener { binding.menuPanel.visibility = View.GONE }
        binding.closeClassroomPanel.setOnClickListener { closeClassroomPanel() }
        binding.classroomPanel.setOnClickListener { closeClassroomPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.classroomName.addTextChangedListener {
            binding.classroomNameTextInputLayout.error = null
        }
    }

    private fun loadGalleryFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    private fun loadViewModel() {
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(this) { image ->
            imageId = image.id
            Constants.imageManager.setImage(image.url, this, binding.ClassroomImage)
            binding.fragmentLayout.visibility = View.GONE
        }
    }

    private fun loadClassroomsToLayout() {
        CoroutineScope(Dispatchers.IO).launch {
            val c = ApiHelper.getTeachersClassrooms(Constants.Teacher.id)
            classrooms = if (c == null) mutableListOf() else c as MutableList<Classroom>

            withContext(Dispatchers.Main) {
                adapter = ClassroomAdapter(myContext, classrooms)
                binding.classroomLayout.adapter = adapter
            }
        }
    }

    private fun closeClassroomPanel() {
        binding.classroomPanel.visibility = View.GONE
        binding.classroomNameTextInputLayout.error = null
        binding.classroomName.text = null
    }

    private suspend fun deleteGroupsInClassroom() {
        val groups = ApiHelper.getGroupsInClassroom(Constants.Classroom.id)
        if (groups != null) {
            for (group in groups) {
                deleteStudentsFromGroup(group)
                ApiHelper.deleteGroup(group.id)
            }
        }
    }

    private suspend fun deleteStudentsFromGroup(group: Group) {
        val studentsGroup = ApiHelper.getStudentGroupsByGroupId(group.id)
        if (studentsGroup != null) {
            for (studentGroup in studentsGroup) {
                ApiHelper.deleteStudentGroup(studentGroup.studentId, studentGroup.groupId)
            }
        }
    }

    private suspend fun deleteStudentsInClassroom() {
        val students = ApiHelper.getStudentsInClassroom(Constants.Classroom.id)
        if (students != null) {
            for (student in students) {
                ApiHelper.deleteStudent(student.id)
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