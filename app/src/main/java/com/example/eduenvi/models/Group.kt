package com.example.eduenvi.models

class Group(
    var id: Int,
    var classroomId: Int,
    var name: String,
    var imageId: Int?,

    var selected: Boolean? = false,
    var inTask: Boolean? = false
){
    override fun equals(other: Any?): Boolean = other is Group && other.id == id
}