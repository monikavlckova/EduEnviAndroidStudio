package com.example.eduenvi.models

import java.util.Date

class Task(
    var id: Int,
    var teacherId: Int,
    var taskTypeId: Int,
    var name: String,
    var text: String,
    var imageId: Int?,
    var deadline:  Date?
)