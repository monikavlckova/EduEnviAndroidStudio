package com.example.eduenvi.models

class Task(
    var id: Int,
    var teacherId: Int,
    var taskTypeId: Int,
    var name: String,
    var text: String,
    var imageId: Int?
)