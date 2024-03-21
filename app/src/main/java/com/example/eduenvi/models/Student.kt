package com.example.eduenvi.models

class Student(
    var id: Int,
    var classroomId: Int,
    var name: String,
    var lastName: String,
    var loginCode: String,
    var imageId: Int?
) {
    override fun equals(other: Any?): Boolean =
        other is Student && other.id == id
}