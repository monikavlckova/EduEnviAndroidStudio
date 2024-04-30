package com.example.eduenvi

import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.Image
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.Task
import com.example.eduenvi.models.TaskType
import com.example.eduenvi.models.Teacher

object Constants {

    private fun getWrongFormatMassage(min: Int): String {
        if (min == 1) return String.format("Musí obsahovať aspoň %d znak!", min)
        if (min <= 4) return String.format("Musí obsahovať aspoň %d znaky!", min)
        return String.format("Musí obsahovať aspoň %d znakov!", min)
    }

    const val MinimalFirstNameLength = 2
    const val MinimalLastNameLength = 2
    const val MinimalUserNameLength = 4
    const val MinimalPasswordLength = 4
    const val MinimalLoginCodeLength = 4
    const val MinimalClassroomNameLength = 2
    const val MinimalGroupNameLength = 2
    const val MinimalTaskNameLength = 2

    const val SetButtonText = "Nastaviť"
    const val SaveButtonTextUpdate = "Uložiť"
    const val SaveButtonTextCreate = "Vytvoriť"
    val WrongFirstNameFormatMessage = getWrongFormatMassage(MinimalFirstNameLength)
    val WrongLastNameFormatMessage = getWrongFormatMassage(MinimalLastNameLength)
    val WrongUserNameFormatMessage = getWrongFormatMassage(MinimalUserNameLength)
    const val WrongUserNameAlreadyExistMessage = "Obsadený login!"
    const val WrongLoginCodeAlreadyExistMessage = "Obsadený prihlasovací kód!"
    const val WrongEmailFormatMessage = "Nesprávny formát!"
    const val WrongEmailAlreadyExistMessage = "Konto s e-mailom uz existuje!"
    const val WrongEmailDoesNotExistMessage = "Konto s e-mailom neexistuje!"
    val WrongPasswordFormatMessage = getWrongFormatMassage(MinimalPasswordLength)
    val WrongLoginCodeFormatMessage = getWrongFormatMassage(MinimalLoginCodeLength)
    const val WrongPasswordsNotSameMessage = "Heslá sa nezhodujú!"
    val WrongClassroomNameFormatMessage = getWrongFormatMassage(MinimalClassroomNameLength)
    val WrongGroupNameFormatMessage = getWrongFormatMassage(MinimalGroupNameLength)
    val WrongTaskNameFormatMessage = getWrongFormatMassage(MinimalTaskNameLength)
    const val WrongUserNameOrPasswordMessage = "Nesprávny login alebo heslo!"
    const val WrongLoginCodeMessage = "Nesprávny prihlasovací kód!"
    const val WrongTaskTypeNotSelected = "Typ úlohy nie je označený!"

    const val SaveError = "Ups, niečo sa pokazilo, zmeny sa nepodarilo uložiť"
    const val DeleteError = "Ups, niečo sa pokazilo, odstránenie sa nepodarilo"

    fun getDeleteClassroomString(c: Classroom) = "Odstrániť triedu " + c.name + "?"
    fun getDeleteStudentString(s: Student) = "Odstrániť žiaka " + s.firstName + " " + s.lastName + "?"
    fun getDeleteGroupString(g: Group): String = "Odstrániť skupinu " + g.name + "?"
    fun getDeleteTaskString(t: Task) = "Odstrániť úlohu " + t.name + "?"
    fun getDeleteStudentFromGroupString(s: Student) =
        "Odstrániť žiaka " + s.firstName + " " + s.lastName + " zo skupiny?"

    fun getDeleteGroupFromStudentString(g: Group) = "Odstrániť žiaka zo skupiny " + g.name + "?"
    fun getDeleteTaskFromGroupString(t: Task) = "Odstrániť úlohu " + t.name + " zo skupiny?"
    fun getDeleteTaskFromStudentString(t: Task) = "Odstrániť žiakovi úlohu " + t.name + "?"
    fun getBackCreatingTaskNotSavedString() = "Naozaj chcete opustiť úpravu úlohy bez uloženia?"

    val emailSender = EmailSender()
    val imageManager = ImageManager()

    var Classroom = Classroom(0, 0, "", null)
    var Group = Group(0, 0, "", null)
    var Student = Student(0, 0, "", "", "", null)
    var Task = Task(0, 0, 0, "", "", null, null, null)
    var TaskType = TaskType(0, "", null)
    var Teacher = Teacher(0, "", "", "", "", "", null)

    var TaskType1Id = 1
    //var TaskType2Id = 2

    var TaskTypeCreatingActivity = mapOf(Pair(TaskType1Id, TaskType1CreatingActivity::class.java)/*,
        Pair(TaskType2Id, TaskType2CreatingActivity::class.java)*/)

    var TaskTypeActivity = mapOf(Pair(TaskType1Id, TaskType1Activity::class.java)/*,
        Pair(TaskType2Id, TaskType2Activity::class.java)*/)

    const val freeImageIndex = 0
    const val wallImageIndex = 1
    const val startImageIndex = 2
    const val itemImageIndex = 3
    var ImageGridResources = mutableListOf(R.color.tile, R.mipmap.wall, R.mipmap.start, R.mipmap.item)
    var ImageGridImages= mutableListOf<Image?>(null, null, null, null)

    var paths = mutableListOf<MutableList<Int>>()

}