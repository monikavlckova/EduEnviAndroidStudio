package com.example.eduenvi.models

class Classroom(
    var id: Int,
    var teacherId: Int,
    var name: String,
    var imageId: Int?
){
    override fun toString(): String {
        return name
    }
}