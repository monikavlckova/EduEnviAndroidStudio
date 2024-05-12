package com.example.eduenvi.models

class Student(
    var id: Int,
    var classroomId: Int?,
    var firstName: String,
    var lastName: String,
    var loginCode: String,
    var imageId: Int?,

    var selected: Boolean? = false,
    var inTask: Boolean? = false
) {
    override fun equals(other: Any?): Boolean = other is Student && other.id == id
}