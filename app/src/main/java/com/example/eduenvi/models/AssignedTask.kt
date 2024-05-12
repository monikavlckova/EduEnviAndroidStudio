package com.example.eduenvi.models

import java.util.Date


class AssignedTask(
    var id: Int,
    var taskId: Int,
    var deadline: Date?,
    var visibleFrom: Date?
)