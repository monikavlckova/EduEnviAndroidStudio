package com.example.eduenvi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.Image
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.Task
import com.example.eduenvi.models.TaskType
import com.example.eduenvi.models.Teacher

class MyViewModel : ViewModel() {

    private var selectedImage: MutableLiveData<Image> = MutableLiveData()
    private var selectedClassroom: MutableLiveData<Classroom> = MutableLiveData()
    private var selectedGroup: MutableLiveData<Group> = MutableLiveData()
    private var selectedStudent: MutableLiveData<Student> = MutableLiveData()
    private var selectedTask: MutableLiveData<Task> = MutableLiveData()
    private var selectedTaskType: MutableLiveData<TaskType> = MutableLiveData()
    private var selectedTeacher: MutableLiveData<Teacher> = MutableLiveData()

    fun setData(image: Image) {
        selectedImage.value = image
    }

    fun setData(classroom: Classroom) {
        selectedClassroom.value = classroom
    }

    fun setData(group: Group) {
        selectedGroup.value = group
    }

    fun setData(student: Student) {
        selectedStudent.value = student
    }

    fun setData(task: Task) {
        selectedTask.value = task
    }

    fun setData(taskType: TaskType) {
        selectedTaskType.value = taskType
    }

    fun setData(teacher: Teacher) {
        selectedTeacher.value = teacher
    }

    fun getSelectedImage() = selectedImage
    fun getSelectedClassroom() = selectedClassroom
    fun getSelectedGroup() = selectedGroup
    fun getSelectedStudent() = selectedStudent
    fun getSelectedTask() = selectedTask
    fun getSelectedTaskType() = selectedTaskType
    fun getSelectedTeacher() = selectedTeacher

}