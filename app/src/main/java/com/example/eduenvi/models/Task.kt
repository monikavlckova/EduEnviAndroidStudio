package com.example.eduenvi.models

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class Task(
    var id: Int,
    var teacherId: Int,
    var taskTypeId: Int,
    var name: String,
    var text: String,
    var imageId: Int?,

    var deadline: Date? = null,
    var visibleFrom: Date? = null,
    var assignedTaskId: Int? = null,

    var isGroup: Boolean? = null,
    var isSolo: Boolean? = null,
    var isTerminated: Boolean? = null
) {
    override fun toString(): String {
        val formatter: DateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        if (visibleFrom == null && deadline == null) return name
        if (visibleFrom == null) {
            val to = formatter.format(deadline!!)
            return "$name \nod:- do:$to"
        }
        val to = formatter.format(deadline!!)
        val from = formatter.format(visibleFrom!!)
        return "$name \nod: $from do:$to"
    }

    override fun equals(other: Any?): Boolean =
        other is Task && other.id == id && other.assignedTaskId == assignedTaskId

}