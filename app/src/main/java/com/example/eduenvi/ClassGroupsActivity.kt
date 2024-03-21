package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.ClassroomGroupsAdapter
import com.example.eduenvi.databinding.ActivityClassGroupsBinding
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.StudentGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClassGroupsActivity : AppCompatActivity() {

    lateinit var binding: ActivityClassGroupsBinding
    private var _creatingNewGroup: Boolean? = null
    private var _delFromGroup: HashSet<Student> = HashSet()
    private var _addToGroup: HashSet<Student> = HashSet()
    private var groups :MutableList<Group>? = null
    private lateinit var adapter: ClassroomGroupsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myContext = this
        CoroutineScope(Dispatchers.IO).launch {
            groups = ApiHelper.getGroupsInClassroom(Constants.Classroom!!.id) as MutableList<Group>?

            withContext(Dispatchers.Main) {
                if (groups != null) {
                    adapter = ClassroomGroupsAdapter(myContext, groups!!)
                    binding.groupsLayout.adapter = adapter
                }
            }
        }
        val classroom = Constants.Classroom!!
        binding.className.text = classroom.name

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }
        binding.tasksButton.setOnClickListener {
            val intent = Intent(this, ClassTasksActivity::class.java)
            startActivity(intent)
        }
        binding.studentsButton.setOnClickListener {
            val intent = Intent(this, ClassStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            _creatingNewGroup = true
            binding.saveButton.text = Constants.SaveButtonTextCreate
            setActiveGroupPanel()
        }

        binding.editButton.setOnClickListener {
            _creatingNewGroup = false
            binding.saveButton.text = Constants.SaveButtonTextUpdate
            binding.groupName.setText(Constants.Group!!.name)
            setActiveGroupPanel()
            binding.editPanel.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            if (validName()) {
                val group =
                    Group(0, Constants.Classroom!!.id, binding.groupName.text.toString(), null)
                var res: Group?
                CoroutineScope(Dispatchers.IO).launch {
                    if (_creatingNewGroup == false) {
                        group.id = Constants.Group!!.id
                        res = ApiHelper.updateGroup(group.id, group)
                        groups!!.remove(Constants.Group)
                    } else {
                        res = ApiHelper.createGroup(group)
                    }
                    Constants.Group = res!!
                    groups!!.add(Constants.Group!!)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataChenged()
                    }
                }
                manageStudents()
                closeGroupPanel()
            }
        }

        binding.deleteButton.setOnClickListener {
            binding.deletePanel.visibility = View.VISIBLE
            binding.editPanel.visibility = View.GONE
            binding.deleteText.text = Constants.GetDeleteGroupString(Constants.Group!!)
        }

        binding.confirmDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.deleteGroup(Constants.Group!!.id)
                groups!!.remove(Constants.Group!!)
                withContext(Dispatchers.Main) {
                    adapter.notifyDataChenged()
                }
            }
            //val intent = Intent(this, ClassGroupsActivity::class.java)
            //startActivity(intent)
        }

        binding.closeGroupPanel.setOnClickListener { closeGroupPanel() }
        binding.closeEditPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.editPanel.setOnClickListener { binding.editPanel.visibility = View.GONE }
        binding.closeDeletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
        binding.deletePanel.setOnClickListener { binding.deletePanel.visibility = View.GONE }
    }

    private fun closeGroupPanel() {
        binding.groupPanel.visibility = View.GONE
        binding.groupNameTextInputLayout.error = null
        binding.groupName.text = null
        empty()
    }

    private fun empty() {
        for (i in binding.chipGroupIn.childCount-1 downTo  0) {
            val chip = binding.chipGroupIn.getChildAt(i)
            binding.chipGroupIn.removeView(chip)
        }

        for (i in binding.chipGroupNotIn.childCount-1 downTo  0) {
            val chip = binding.chipGroupNotIn.getChildAt(i)
            binding.chipGroupNotIn.removeView(chip)
        }
    }

    private fun setActiveGroupPanel() {
        binding.groupPanel.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            var studentsInGroup: List<Student>? = null
            var studentsNotInGroup: List<Student>? =
                ApiHelper.getStudentsInClassroom(Constants.Classroom!!.id)
            if (_creatingNewGroup == false) {
                studentsInGroup = ApiHelper.getStudentsInGroup(Constants.Group!!.id)
                studentsNotInGroup = ApiHelper.getStudentsFromClassroomNotInGroup(
                    Constants.Classroom!!.id,
                    Constants.Group!!.id
                )
            }
            withContext(Dispatchers.Main) {
                addStudentsToLists(studentsInGroup, studentsNotInGroup)
            }
        }
    }

    private fun addStudentsToLists(studentsInGroup: List<Student>?, studentsNotInGroup: List<Student>?) {
        studentsInGroup?.forEach { student ->
            addStudentToList(student, binding.chipGroupIn, true)
        }
        studentsNotInGroup?.forEach { student ->
            addStudentToList(student, binding.chipGroupNotIn, false)
        }
    }

    private fun addStudentToList(student: Student, chipGroup: ChipGroup, isInGroup: Boolean) {
        var addedInGroup = isInGroup
        val tagName = student.name + " " + student.lastName
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
        for (student in _addToGroup)
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.createStudentGroup(StudentGroup(Constants.Group!!.id, student.id))
            }
        for (student in _delFromGroup)
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.deleteStudentGroup(student.id, Constants.Group!!.id)
            }

        _delFromGroup = HashSet()
        _addToGroup = HashSet()
    }

    private fun validName(): Boolean {

        if (binding.groupName.text.toString().length < Constants.MinimalGroupNameLength) {
            binding.groupNameTextInputLayout.error = Constants.WrongGroupNameFormatMessage
            return false
        }

        return true
    }
}