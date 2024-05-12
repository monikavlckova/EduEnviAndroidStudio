package com.example.eduenvi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.models.AssignedTask
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.GroupAssignedTask
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.StudentAssignedTask
import com.example.eduenvi.models.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AssignTaskFragment : Fragment() {

    private var studentsInClassroom: List<Student> = listOf()
    private var groupsInClassroom: List<Group> = listOf()

    private var _creatingNewAssignedTask = false
    private var settingDeadline = false
    private var deadline: Date? = null
    private var visibleFrom: Date? = null

    private lateinit var dateTimeTaskVisibleFrom: TextView
    private lateinit var dateTimeTaskDeadline: TextView
    private lateinit var dateTimePickerPanel: RelativeLayout
    private lateinit var setDate: Button
    private lateinit var dateLayout: ConstraintLayout
    private lateinit var timeLayout: ConstraintLayout
    private lateinit var setTime: Button
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var closeDate: Button
    private lateinit var closeTime: Button
    private lateinit var taskName: AutoCompleteTextView
    private lateinit var taskNameTextInputLayout: TextInputLayout
    private lateinit var taskNameEdit: TextView
    private lateinit var taskPanelInformation: LinearLayout
    private lateinit var chipGroupGroups: ChipGroup
    private lateinit var chipGroupStudents: ChipGroup
    private lateinit var datesError: TextView
    private lateinit var view: View

    private var loaded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_assign_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
    }

    fun load() {
        if (loaded) return

        loaded = true
        dateTimeTaskVisibleFrom = view.findViewById(R.id.dateTimeTaskVisibleFrom)
        dateTimeTaskDeadline = view.findViewById(R.id.dateTimeTaskDeadline)
        dateTimePickerPanel = view.findViewById(R.id.dateTimePickerPanel)
        setDate = view.findViewById(R.id.setDate)
        dateLayout = view.findViewById(R.id.dateLayout)
        timeLayout = view.findViewById(R.id.timeLayout)
        setTime = view.findViewById(R.id.setTime)
        datePicker = view.findViewById(R.id.datePicker)
        timePicker = view.findViewById(R.id.timePicker)
        closeDate = view.findViewById(R.id.closeDate)
        closeTime = view.findViewById(R.id.closeTime)
        taskName = view.findViewById(R.id.taskName)
        taskNameTextInputLayout = view.findViewById(R.id.taskNameTextInputLayout)
        taskNameEdit = view.findViewById(R.id.taskNameEdit)
        taskPanelInformation = view.findViewById(R.id.taskPanelInformation)
        chipGroupGroups = view.findViewById(R.id.chipGroupGroups)
        chipGroupStudents = view.findViewById(R.id.chipGroupStudents)
        datesError = view.findViewById(R.id.datesError)

        CoroutineScope(Dispatchers.IO).launch {
            val sic = ApiHelper.getStudentsInClassroom(Constants.Classroom.id)
            studentsInClassroom = sic ?: listOf()
            val gis = ApiHelper.getGroupsInClassroom(Constants.Classroom.id)
            groupsInClassroom = gis ?: listOf()
        }


        dateTimeTaskVisibleFrom.setOnClickListener {
            settingDeadline = false
            dateTimePickerPanel.visibility = View.VISIBLE
        }

        dateTimeTaskDeadline.setOnClickListener {
            settingDeadline = true
            dateTimePickerPanel.visibility = View.VISIBLE
        }

        setDate.setOnClickListener {
            dateLayout.visibility = View.GONE
            timeLayout.visibility = View.VISIBLE
        }

        setTime.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            cal.set(Calendar.YEAR, datePicker.year)
            cal.set(Calendar.MONTH, datePicker.month)
            cal.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            val sdf = SimpleDateFormat("dd. MM. yyyy hh:mm")
            val datetime: String = sdf.format(cal.time)
            if (settingDeadline) {
                dateTimeTaskDeadline.text = datetime
                deadline = cal.time
            } else {
                dateTimeTaskVisibleFrom.text = datetime
                visibleFrom = cal.time
            }
            closeDateTimePanel()
        }

        dateTimePickerPanel.setOnClickListener { closeDateTimePanel() }
        closeDate.setOnClickListener { closeDateTimePanel() }
        closeTime.setOnClickListener { closeDateTimePanel() }
        taskName.addTextChangedListener { taskNameTextInputLayout.error = null }
    }

    fun save(): Boolean {
        val validName = validName()
        val validDates = validDates()
        if (validName && validDates) {
            val taskId = Constants.Task.id
            val assignedTaskId = Constants.Task.assignedTaskId ?: 0
            val assignedTask = AssignedTask(assignedTaskId, taskId, deadline, visibleFrom)
            saveAssignedTask(assignedTask)
            closeTaskPanel()
            return true
        }
        return false
    }

    fun setTaskPanelCreatingNew() {
        _creatingNewAssignedTask = true
        setDateTimeText()
        setTasksDropdown()
        deadline = null
        visibleFrom = null
        taskNameTextInputLayout.visibility = View.VISIBLE
        taskNameEdit.visibility = View.GONE
        taskName.text = null
        taskPanelInformation.visibility = View.GONE
    }

    fun setTaskPanelUpdating() {
        _creatingNewAssignedTask = false
        setDateTimeText()
        deadline = Constants.Task.deadline
        visibleFrom = Constants.Task.visibleFrom
        taskNameTextInputLayout.visibility = View.GONE
        taskNameEdit.visibility = View.VISIBLE
        taskNameEdit.setText(Constants.Task.name)
        taskPanelInformation.visibility = View.VISIBLE
        empty()
        loadChips(Constants.Task.assignedTaskId)
    }

    private fun setTasksDropdown() {
        CoroutineScope(Dispatchers.IO).launch {
            val teachersTasks = ApiHelper.getTeachersTasksAndAssignedTasks(Constants.Teacher.id)
            withContext(Dispatchers.Main) {
                if (teachersTasks != null) {
                    val listAdapter =
                        ArrayAdapter(requireActivity(), R.layout.dropdown_list_item, teachersTasks)
                    taskName.setAdapter(listAdapter)
                    taskName.setOnItemClickListener { adapterView, _, i, _ ->
                        val task = adapterView.getItemAtPosition(i) as Task
                        Constants.Task = task
                        taskName.setText(task.name)
                        deadline = task.deadline
                        visibleFrom = task.visibleFrom
                        setDateTimeText()
                        if (task.assignedTaskId != null) _creatingNewAssignedTask = false
                        empty()
                        loadChips(task.assignedTaskId)
                        taskPanelInformation.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setDateTimeText() {
        val sdf = SimpleDateFormat("dd. MM. yyyy hh:mm")

        dateTimeTaskDeadline.text = Constants.SetButtonText
        dateTimeTaskVisibleFrom.text = Constants.SetButtonText
        if (Constants.Task.deadline != null) {
            dateTimeTaskDeadline.text = sdf.format(Constants.Task.deadline!!)
        }
        if (Constants.Task.visibleFrom != null) {
            dateTimeTaskVisibleFrom.text = sdf.format(Constants.Task.visibleFrom!!)
        }

    }

    private fun saveAssignedTask(assignedTask: AssignedTask) {
        var res: AssignedTask?
        CoroutineScope(Dispatchers.IO).launch {
            res = (if (_creatingNewAssignedTask) {
                ApiHelper.createAssignedTask(assignedTask)
            } else {
                ApiHelper.updateAllAssignedTask(assignedTask.id, assignedTask)
            })
            if (res != null) {
                manageStudentAssignedTask(res!!)
                manageGroupAssignedTask(res!!)
            }
            withContext(Dispatchers.Main) {
                if (res != null) {
                    Constants.Task.deadline = res!!.deadline
                    Constants.Task.visibleFrom = res!!.visibleFrom
                    Constants.Task.assignedTaskId = res!!.id
                } else {
                    Toast.makeText(requireActivity(), Constants.SaveError, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun manageStudentAssignedTask(assignedTask: AssignedTask) {
        for (student in studentsInClassroom.filter { student -> student.selected == true && student.inTask == false }) {
            ApiHelper.createStudentAssignedTask(StudentAssignedTask(student.id, assignedTask.id))
        }
        for (student in studentsInClassroom.filter { student -> student.selected == false && student.inTask == true }) {
            ApiHelper.deleteStudentAssignedTask(student.id, assignedTask.id)
        }
    }

    private suspend fun manageGroupAssignedTask(assignedTask: AssignedTask) {
        for (group in groupsInClassroom.filter { group -> group.selected == true && group.inTask == false }) {
            ApiHelper.createGroupAssignedTask(GroupAssignedTask(group.id, assignedTask.id))
        }
        for (group in groupsInClassroom.filter { group -> group.selected == false && group.inTask == true }) {
            ApiHelper.deleteGroupAssignedTask(group.id, assignedTask.id)
        }
    }

    private fun closeTaskPanel() {
        taskNameTextInputLayout.error = null
        taskName.text = null
        empty()
    }

    private fun empty() {
        for (i in chipGroupGroups.childCount - 1 downTo 0) {
            val chip = chipGroupGroups.getChildAt(i)
            chipGroupGroups.removeView(chip)
        }

        for (i in chipGroupStudents.childCount - 1 downTo 0) {
            val chip = chipGroupStudents.getChildAt(i)
            chipGroupStudents.removeView(chip)
        }
    }

    private fun loadChips(assignedTaskId: Int?) {
        studentsInClassroom.map {
            it.selected = false
            it.inTask = false
        }
        groupsInClassroom.map {
            it.selected = false
            it.inTask = false
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (assignedTaskId != null) {

                val studentsAssigned = ApiHelper.getStudentsInAssignedTask(assignedTaskId)
                studentsInClassroom.map {
                    val inTask = studentsAssigned!!.contains(it)
                    it.selected = inTask
                    it.inTask = inTask
                }
                val groupsAssigned = ApiHelper.getGroupsInAssignedTask(assignedTaskId)
                groupsInClassroom.map {
                    val inTask = groupsAssigned!!.contains(it)
                    it.selected = inTask
                    it.inTask = inTask
                }
            }
            withContext(Dispatchers.Main) {
                fillChipGroups()
            }
        }
    }

    private fun fillChipGroups() {
        studentsInClassroom.forEachIndexed { index, student ->
            createStudentChips(index, student, chipGroupStudents)
        }
        groupsInClassroom.forEachIndexed { index, group ->
            createGroupChips(index, group, chipGroupGroups)
        }
    }

    private fun createStudentChips(index: Int, student: Student, chipGroup: ChipGroup) {
        Log.v("MYLOG", "je?: ${student.inTask}")
        val tagName = student.firstName + " " + student.lastName
        val chip = Chip(requireActivity())
        chip.isCheckable = true
        chip.isChecked = student.selected!!
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chip.isCloseIconVisible = false
        chip.setOnClickListener {
            studentsInClassroom[index].selected = !studentsInClassroom[index].selected!!
        }
        chipGroup.addView(chip)
    }

    private fun createGroupChips(index: Int, group: Group, chipGroup: ChipGroup) {
        val tagName = group.name
        val chip = Chip(requireActivity())
        chip.isCheckable = true
        chip.isChecked = group.selected!!
        chip.text = tagName
        chip.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chip.isCloseIconVisible = false
        chip.setOnClickListener {
            groupsInClassroom[index].selected = !groupsInClassroom[index].selected!!
        }
        chipGroup.addView(chip)
    }

    private fun closeDateTimePanel() {
        dateTimePickerPanel.visibility = View.GONE
        dateLayout.visibility = View.VISIBLE
        timeLayout.visibility = View.GONE
    }

    private fun validName(): Boolean { //TODO zmen, ci je nezmenene nejak
        return taskName.text != null
    }

    private fun validDates(): Boolean {
        datesError.visibility = View.GONE
        if (deadline == null || visibleFrom == null || visibleFrom!! < deadline) return true
        datesError.text = Constants.WrongDatesDeadlineIsSoonerThenVisibility
        datesError.visibility = View.VISIBLE
        return false
    }
}