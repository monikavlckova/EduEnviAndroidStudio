package com.example.eduenvi

import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.Task
import com.example.eduenvi.models.Teacher


object Constants {

    private fun GetWrongFormatMassage(min: Int): String {
        if (min == 1) return String.format("Musí obsahovať aspoň %d znak!", min)
        if (min <= 4) return String.format("Musí obsahovať aspoň %d znaky!", min)
        return String.format("Musí obsahovať aspoň %d znakov!", min)
    }

    val MinimalFirstNameLength = 2
    val MinimalLastNameLength = 2
    val MinimalUserNameLength = 4
    val MinimalPasswordLength = 4
    val MinimalLoginCodeLength = 4
    val MinimalClassroomNameLength = 2
    val MinimalGroupNameLength = 2
    val MinimalTaskNameLength = 2

    val SaveButtonTextUpdate = "Uložiť"
    val SaveButtonTextCreate = "Vytvoriť"
    val WrongFirstNameFormatMessage = GetWrongFormatMassage(MinimalFirstNameLength)
    val WrongLastNameFormatMessage = GetWrongFormatMassage(MinimalLastNameLength)
    val WrongUserNameFormatMessage = GetWrongFormatMassage(MinimalUserNameLength)
    val WrongUserNameAlreadyExistMessage = "Obsadený login!"
    val WrongLoginCodeAlreadyExistMessage = "Obsadený prihlasovací kód!"
    val WrongEmailFormatMessage = "Nesprávny formát!"
    val WrongEmailAlreadyExistMessage = "Konto s e-mailom uz existuje!"
    val WrongEmailDoesNotExistMessage = "Konto s e-mailom neexistuje!"
    val WrongPasswordFormatMessage = GetWrongFormatMassage(MinimalPasswordLength)
    val WrongLoginCodeFormatMessage = GetWrongFormatMassage(MinimalLoginCodeLength)
    val WrongPasswordsNotSameMessage = "Heslá sa nezhodujú!"
    val WrongClassroomNameFormatMessage = GetWrongFormatMassage(MinimalClassroomNameLength)
    val WrongGroupNameFormatMessage = GetWrongFormatMassage(MinimalGroupNameLength)
    val WrongTaskNameFormatMessage = GetWrongFormatMassage(MinimalTaskNameLength)
    val WrongUserNameOrPasswordMessage = "Nesprávny login alebo heslo!"
    val WrongLoginCodeMessage = "Nesprávny prihlasovací kód!"

    val LastSceneName = "First"

    fun GetDeleteClassroomString(classroom: Classroom): String {
        return "Odstrániť triedu " + classroom.name + "?"
    }

    fun GetDeleteStudentString(student: Student): String {
        return "Odstrániť žiaka " + student.name + " " + student.lastName + "?"
    }

    fun GetDeleteGroupString(group: Group): String {
        return "Odstrániť skupinu " + group.name + "?"
    }

    fun GetDeleteTaskString(task: Task): String {
        return "Odstrániť úlohu " + task.name + "?"
    }

    fun GetDeleteStudentFromGroupString(student: Student): String {
        return "Odstrániť žiaka " + student.name + " " + student.lastName + " zo skupiny?"
    }

    fun GetDeleteGroupFromStudentString(group: Group): String {
        return "Odstrániť žiaka zo skupiny " + group.name + "?"
    }

    fun GetDeleteTaskFromGroupString(task: Task): String {
        return "Odstrániť úlohu " + task.name + " zo skupiny?"
    }

    fun GetDeleteTaskFromStudentString(task: Task): String {
        return "Odstrániť žiakovi úlohu " + task.name + "?"
    }
    /*
        public static Sprite GetSprite(path:String):String
        {
            return Resources.Load<Sprite>(path)
        }

            public static readonly Sprite XSprite = Resources.Load<Sprite>("Sprites/close")
            public static readonly Sprite DotsSprite = Resources.Load<Sprite>("Sprites/more")
            public static readonly Sprite PlusSprite = Resources.Load<Sprite>("Sprites/plus")
            public static readonly Sprite GroupSprite = Resources.Load<Sprite>("Sprites/groupHighlighted")
            public static readonly Sprite StudentsSprite = Resources.Load<Sprite>("Sprites/studentsHighlighted")
            public static readonly Sprite TaskSprite = Resources.Load<Sprite>("Sprites/taskHighlighted")
            public static readonly Sprite ShowSprite = Resources.Load<Sprite>("Sprites/show")
            public static readonly Sprite HideSprite = Resources.Load<Sprite>("Sprites/hide")
            public static readonly Sprite EdgeValueType1Sprite = Resources.Load<Sprite>("Sprites/TaskType1/rocket")
            public static readonly Sprite EdgeValueType2Sprite = Resources.Load<Sprite>("Sprites/TaskType1/spaceship")
            public static readonly Sprite EdgeValueType3Sprite = Resources.Load<Sprite>("Sprites/TaskType1/ufo")

         */

    val emailSender = EmailSender()
    val dbImageManager = DBImageManager()

    var Classroom: Classroom = Classroom(0, 0, "", null)
    var Group: Group = Group(0, 0, "", null)
    var Student: Student = Student(0, 0, "", "", "",null)
    var Task: Task = Task(0, 0, 0, "", "", null)
    var Teacher: Teacher = Teacher(0, "", "", "", "", "", null)

}