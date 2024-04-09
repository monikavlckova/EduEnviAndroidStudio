package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.ClassroomGroupsAdapter
import com.example.eduenvi.databinding.ActivityClassroomGroupsBinding
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.StudentGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassroomGroupsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassroomGroupsBinding
    private var _creatingNewGroup: Boolean? = null
    private var _delFromGroup: HashSet<Student> = HashSet()
    private var _addToGroup: HashSet<Student> = HashSet()
    private var groups: MutableList<Group>? = null
    private lateinit var adapter: ClassroomGroupsAdapter
    private val myContext = this
    private var imageId: Int? = null
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setImageGalleryFragment()
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.getSelectedImage().observe(this) { image ->
            imageId = image.id
            Constants.imageManager.setImage(image.url, this, binding.GroupImage)
            binding.fragmentLayout.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.IO).launch {
            groups = ApiHelper.getGroupsInClassroom(Constants.Classroom.id) as MutableList<Group>

            withContext(Dispatchers.Main) {
                if (groups == null) groups = mutableListOf()
                adapter = ClassroomGroupsAdapter(myContext, groups!!)
                binding.groupsLayout.adapter = adapter
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
        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, ClassroomStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNewGroup = true
            imageId = null
            Constants.imageManager.setImage("", this, binding.GroupImage)
            binding.saveButton.text = Constants.SaveButtonTextCreate
            setActiveGroupPanel()
        }

        binding.editButton.setOnClickListener {
            _creatingNewGroup = false
            imageId = Constants.Group.imageId
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.groupName.setText(Constants.Group.name)
            Constants.imageManager.setImage(Constants.Group.imageId, this, binding.GroupImage)
            setActiveGroupPanel()
            binding.editPanel.visibility = View.GONE
        }

        binding.editGroupImage.setOnClickListener {
            binding.fragmentLayout.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            if (validName()) {
                val group =
                    Group(
                        0,
                        Constants.Classroom.id,
                        binding.groupName.text.toString(),
                        imageId
                    )
                var res: Group?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNewGroup == false) {
                        group.id = Constants.Group.id
                        res = ApiHelper.updateGroup(group.id, group)
                        groups?.remove(Constants.Group)
                    } else res = ApiHelper.createGroup(group)

                    withContext(Dispatchers.Main) {
                        if (res != null) {
                            Constants.Group = res!!
                            groups?.add(res!!)
                        } else Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG)
                            .show()
                        manageStudents()
                        adapter.notifyDataChanged()
                    }
                }
                closeGroupPanel()
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.getDeleteGroupString(Constants.Group)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val res = ApiHelper.deleteGroup(Constants.Group.id)
                withContext(Dispatchers.Main) {
                    if (res != null) {
                        groups?.remove(Constants.Group)
                    } else
                        Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                    adapter.notifyDataChanged()
                    binding.deletePanel.visibility = View.GONE
                }
            }
            //val intent = Intent(this, ClassroomGroupsActivity::class.java)
            //startActivity(intent)
        }

        binding.closeFragmentButton.setOnClickListener {
            binding.fragmentLayout.visibility = View.GONE
            binding.editPanel.visibility = View.VISIBLE
            //supportFragmentManager.popBackStack()
        }

        binding.closeGroupPanel.setOnClickListener { closeGroupPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }

        binding.groupName.addTextChangedListener { binding.groupNameTextInputLayout.error = null }
    }

    private fun setImageGalleryFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = ImageGalleryFragment()
        fragmentTransaction.add(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun closeGroupPanel() {
        binding.groupPanel.visibility = View.GONE
        binding.mainPanel.visibility = View.VISIBLE
        binding.groupNameTextInputLayout.error = null
        binding.groupName.text = null
        empty()
    }

    private fun empty() {
        for (i in binding.chipGroupIn.childCount - 1 downTo 0) {
            val chip = binding.chipGroupIn.getChildAt(i)
            binding.chipGroupIn.removeView(chip)
        }

        for (i in binding.chipGroupNotIn.childCount - 1 downTo 0) {
            val chip = binding.chipGroupNotIn.getChildAt(i)
            binding.chipGroupNotIn.removeView(chip)
        }
    }

    private fun setActiveGroupPanel() {
        binding.groupPanel.visibility = View.VISIBLE
        binding.mainPanel.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            var studentsInGroup: List<Student>? = null
            var studentsNotInGroup: List<Student>? =
                ApiHelper.getStudentsInClassroom(Constants.Classroom.id)
            if (_creatingNewGroup == false) {
                studentsInGroup = ApiHelper.getStudentsInGroup(Constants.Group.id)
                studentsNotInGroup = ApiHelper.getStudentsFromClassroomNotInGroup(
                    Constants.Classroom.id,
                    Constants.Group.id
                )
            }
            withContext(Dispatchers.Main) {
                addStudentsToLists(studentsInGroup, studentsNotInGroup)
            }
        }
    }

    private fun addStudentsToLists(
        studentsInGroup: List<Student>?,
        studentsNotInGroup: List<Student>?
    ) {
        studentsInGroup?.forEach { student ->
            addStudentToList(student, binding.chipGroupIn, true)
        }
        studentsNotInGroup?.forEach { student ->
            addStudentToList(student, binding.chipGroupNotIn, false)
        }
    }

    private fun addStudentToList(student: Student, chipGroup: ChipGroup, isInGroup: Boolean) {
        var addedInGroup = isInGroup
        val tagName = student.firstName + " " + student.lastName
        val chip = Chip(this)
        /*val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)*/
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        if (isInGroup) chip.setCloseIconResource(R.drawable.baseline_close_24)
        else chip.setCloseIconResource(R.drawable.baseline_add_24)
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            //Constants.Student = student
            if (addedInGroup) {
                chip.setCloseIconResource(R.drawable.baseline_add_24)
                binding.chipGroupIn.removeView(chip)
                binding.chipGroupNotIn.addView(chip)
                addedInGroup = false
                if (isInGroup) _delFromGroup.add(student)
                else _addToGroup.remove(student)
            } else {
                chip.setCloseIconResource(R.drawable.baseline_close_24)
                binding.chipGroupNotIn.removeView(chip)
                binding.chipGroupIn.addView(chip)
                addedInGroup = true
                if (!isInGroup) _addToGroup.add(student)
                else _delFromGroup.remove(student)
            }
        }
        chipGroup.addView(chip)
    }

    private fun manageStudents() {
        var showSaveToast = false
        var showDeleteToast = false
        CoroutineScope(Dispatchers.IO).launch {
            for (student in _addToGroup) {
                val res = ApiHelper.createStudentGroup(StudentGroup(student.id, Constants.Group.id))
                if (res == null) showSaveToast = true
            }
            for (student in _delFromGroup) {
                val res = ApiHelper.deleteStudentGroup(student.id, Constants.Group.id)
                if (res == null) showDeleteToast = true
            }
            withContext(Dispatchers.Main) {
                if (showSaveToast)
                    Toast.makeText(myContext, Constants.SaveError, Toast.LENGTH_LONG).show()
                if (showDeleteToast)
                    Toast.makeText(myContext, Constants.DeleteError, Toast.LENGTH_LONG).show()
                _delFromGroup = HashSet()
                _addToGroup = HashSet()
            }
        }
    }

    private fun validName(): Boolean {
        if (binding.groupName.text.toString().length < Constants.MinimalGroupNameLength) {
            binding.groupNameTextInputLayout.error = Constants.WrongGroupNameFormatMessage
            return false
        }

        return true
    }
}