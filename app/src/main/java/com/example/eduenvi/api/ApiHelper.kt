package com.example.eduenvi.api


import android.util.Log
import com.example.eduenvi.models.Board
import com.example.eduenvi.models.Classroom
import com.example.eduenvi.models.ClassroomTask
import com.example.eduenvi.models.Group
import com.example.eduenvi.models.GroupTask
import com.example.eduenvi.models.Image
import com.example.eduenvi.models.Student
import com.example.eduenvi.models.StudentGroup
import com.example.eduenvi.models.StudentTask
import com.example.eduenvi.models.Task
import com.example.eduenvi.models.TaskType
import com.example.eduenvi.models.Teacher
import com.example.eduenvi.models.Tile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object ApiHelper {

    private suspend inline fun <reified T> performApiCall(crossinline call: suspend () -> retrofit2.Response<T>): T? {
        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.v("DB", "Error: ${response.code()}")
                    null
                }
            } catch (e: HttpException) {
                Log.v("DB", "http error ${e.message}")
                null
            } catch (e: IOException) {
                Log.v("DB", "app error ${e.message}")
                null
            }
        }
    }

    suspend fun getClassroom(id: Int): Classroom? {
        return performApiCall { RetrofitInstance.api.getClassroom(id) }
    }


    suspend fun getAllClassrooms(): List<Classroom>? {
        return performApiCall { RetrofitInstance.api.getAllClassrooms() }
    }


    suspend fun getTeachersClassrooms(teacherId: Int): List<Classroom>? {
        return performApiCall { RetrofitInstance.api.getTeachersClassroom(teacherId) }
    }


    suspend fun createClassroom(classroom: Classroom): Classroom? {
        return performApiCall { RetrofitInstance.api.createClassroom(classroom) }
    }


    suspend fun updateAllClassroom(id: Int, classroom: Classroom): Classroom? {
        return performApiCall { RetrofitInstance.api.updateAllClassroom(id, classroom) }
    }

    suspend fun updateClassroom(id: Int, classroom: Classroom): Classroom? {
        return performApiCall { RetrofitInstance.api.updateClassroom(id, classroom) }
    }


    suspend fun deleteClassroom(id: Int): Classroom? {
        return performApiCall { RetrofitInstance.api.deleteClassroom(id) }
    }


    suspend fun getClassroomTask(classroomId: Int, taskId: Int): ClassroomTask? {
        return performApiCall { RetrofitInstance.api.getClassroomTask(classroomId, taskId) }
    }


    suspend fun createClassroomTask(classroomTask: ClassroomTask): ClassroomTask? {
        return performApiCall { RetrofitInstance.api.createClassroomTask(classroomTask) }
    }

    suspend fun updateAllClassroomTask(
        classroomId: Int,
        taskId: Int,
        classroomTask: ClassroomTask
    ): ClassroomTask? {
        return performApiCall {
            RetrofitInstance.api.updateAllClassroomTask(
                classroomId,
                taskId,
                classroomTask
            )
        }
    }

    suspend fun deleteClassroomTask(classroomId: Int, taskId: Int): ClassroomTask? {
        return performApiCall { RetrofitInstance.api.deleteClassroomTask(classroomId, taskId) }
    }


    suspend fun getGroup(id: Int): Group? {
        return performApiCall { RetrofitInstance.api.getGroup(id) }
    }


    suspend fun getAllGroups(): List<Group>? {
        return performApiCall { RetrofitInstance.api.getAllGroups() }
    }


    suspend fun getGroupsInClassroom(classroomId: Int): List<Group>? {
        return performApiCall { RetrofitInstance.api.getGroupsInClassroom(classroomId) }
    }


    suspend fun getStudentsGroups(id: Int): List<Group>? {
        return performApiCall { RetrofitInstance.api.getStudentsGroups(id) }
    }


    suspend fun getGroupsFromInClassroomNotInStudent(
        classroomId: Int,
        studentId: Int
    ): List<Group>? {
        return performApiCall {
            RetrofitInstance.api.getGroupsFromInClassroomNotInStudent(
                classroomId,
                studentId
            )
        }
    }


    suspend fun createGroup(group: Group): Group? {
        return performApiCall { RetrofitInstance.api.createGroup(group) }
    }


    suspend fun updateAllGroup(id: Int, group: Group): Group? {
        return performApiCall { RetrofitInstance.api.updateAllGroup(id, group) }
    }

    suspend fun updateGroup(id: Int, group: Group): Group? {
        return performApiCall { RetrofitInstance.api.updateGroup(id, group) }
    }


    suspend fun deleteGroup(id: Int): Group? {
        return performApiCall { RetrofitInstance.api.deleteGroup(id) }
    }


    suspend fun getGroupTask(groupId: Int, taskId: Int): GroupTask? {
        return performApiCall { RetrofitInstance.api.getGroupTask(groupId, taskId) }
    }


    suspend fun createGroupTask(groupTask: GroupTask): GroupTask? {
        return performApiCall { RetrofitInstance.api.createGroupTask(groupTask) }
    }


    suspend fun updateAllGroupTask(groupId: Int, taskId: Int, groupTask: GroupTask): GroupTask? {
        return performApiCall {
            RetrofitInstance.api.updateAllGroupTask(
                groupId,
                taskId,
                groupTask
            )
        }
    }


    suspend fun deleteGroupTask(groupId: Int, taskId: Int): GroupTask? {
        return performApiCall { RetrofitInstance.api.deleteGroupTask(groupId, taskId) }
    }


    suspend fun getStudent(id: Int): Student? {
        return performApiCall { RetrofitInstance.api.getStudent(id) }
    }


    suspend fun getStudentByLoginCode(loginCode: String): Student? {
        return performApiCall { RetrofitInstance.api.getStudentByLoginCode(loginCode) }
    }


    suspend fun getAllStudents(): List<Student>? {
        return performApiCall { RetrofitInstance.api.getAllStudents() }
    }


    suspend fun getStudentsInGroup(groupId: Int): List<Student>? {
        return performApiCall { RetrofitInstance.api.getStudentsInGroup(groupId) }
    }


    suspend fun getStudentsFromClassroomNotInGroup(
        classroomId: Int,
        groupId: Int
    ): List<Student>? {
        return performApiCall {
            RetrofitInstance.api.getStudentsFromClassroomNotInGroup(
                classroomId,
                groupId
            )
        }
    }


    suspend fun getStudentsInClassroom(classroomId: Int): List<Student>? {
        return performApiCall { RetrofitInstance.api.getStudentsInClassroom(classroomId) }
    }


    suspend fun createStudent(student: Student): Student? {
        return performApiCall { RetrofitInstance.api.createStudent(student) }
    }


    suspend fun updateAllStudent(id: Int, student: Student): Student? {
        return performApiCall { RetrofitInstance.api.updateAllStudent(id, student) }
    }

    suspend fun updateStudent(id: Int, student: Student): Student? {
        return performApiCall { RetrofitInstance.api.updateStudent(id, student) }
    }


    suspend fun deleteStudent(id: Int): Student? {
        return performApiCall { RetrofitInstance.api.deleteStudent(id) }
    }


    suspend fun getStudentGroup(studentId: Int, groupId: Int): StudentGroup? {
        return performApiCall { RetrofitInstance.api.getStudentGroup(studentId, groupId) }
    }


    suspend fun createStudentGroup(studentGroup: StudentGroup): StudentGroup? {
        return performApiCall { RetrofitInstance.api.createStudentGroup(studentGroup) }
    }


    suspend fun updateAllStudentGroup(
        studentId: Int,
        groupId: Int,
        studentGroup: StudentGroup
    ): StudentGroup? {
        return performApiCall {
            RetrofitInstance.api.updateAllStudentGroup(
                studentId,
                groupId,
                studentGroup
            )
        }
    }


    suspend fun deleteStudentGroup(studentId: Int, groupId: Int): StudentGroup? {
        return performApiCall { RetrofitInstance.api.deleteStudentGroup(studentId, groupId) }
    }


    suspend fun getStudentTask(studentId: Int, taskId: Int): StudentTask? {
        return performApiCall { RetrofitInstance.api.getStudentTask(studentId, taskId) }
    }


    suspend fun createStudentTask(studentTask: StudentTask): StudentTask? {
        return performApiCall { RetrofitInstance.api.createStudentTask(studentTask) }
    }


    suspend fun updateAllStudentTask(
        studentId: Int,
        taskId: Int,
        studentTask: StudentTask
    ): StudentTask? {
        return performApiCall {
            RetrofitInstance.api.updateAllStudentTask(
                studentId,
                taskId,
                studentTask
            )
        }
    }


    suspend fun deleteStudentTask(studentId: Int, taskId: Int): StudentTask? {
        return performApiCall { RetrofitInstance.api.deleteStudentTask(studentId, taskId) }
    }


    suspend fun getTask(id: Int): Task? {
        return performApiCall { RetrofitInstance.api.getTask(id) }
    }


    suspend fun getTasksInClassroom(classroomId: Int): List<Task>? {
        return performApiCall { RetrofitInstance.api.getTasksInClassroom(classroomId) }
    }


    suspend fun getGroupsTasks(groupId: Int): List<Task>? {
        return performApiCall { RetrofitInstance.api.getGroupsTasks(groupId) }
    }


    suspend fun getStudentsTasks(studentId: Int): List<Task>? {
        return performApiCall { RetrofitInstance.api.getStudentsTasks(studentId) }
    }


    suspend fun getTasksFromTeacherNotInGroup(teacherId: Int, groupId: Int): List<Task>? {
        return performApiCall {
            RetrofitInstance.api.getTasksFromTeacherNotInGroup(
                teacherId,
                groupId
            )
        }
    }


    suspend fun getTasksFromTeacherNotInStudent(teacherId: Int, studentId: Int): List<Task>? {
        return performApiCall {
            RetrofitInstance.api.getTasksFromTeacherNotInStudent(
                teacherId,
                studentId
            )
        }
    }

    suspend fun getTasksInStudentsGroups(studentId: Int): List<Task>? {
        return performApiCall { RetrofitInstance.api.getTasksInStudentsGroups(studentId) }
    }


    suspend fun createTask(task: Task): Task? {
        return performApiCall { RetrofitInstance.api.createTask(task) }
    }


    suspend fun updateAllTask(id: Int, task: Task): Task? {
        return performApiCall { RetrofitInstance.api.updateAllTask(id, task) }
    }

    suspend fun updateTask(id: Int, task: Task): Task? {
        return performApiCall { RetrofitInstance.api.updateTask(id, task) }
    }


    suspend fun deleteTask(id: Int): Task? {
        return performApiCall { RetrofitInstance.api.deleteTask(id) }
    }


    suspend fun getTaskType(id: Int): TaskType? {
        return performApiCall { RetrofitInstance.api.getTaskType(id) }
    }

    suspend fun getAllTaskTypes(): List<TaskType>? {
        return performApiCall { RetrofitInstance.api.getAllTaskTypes() }
    }


    suspend fun createTaskType(taskType: TaskType): TaskType? {
        return performApiCall { RetrofitInstance.api.createTaskType(taskType) }
    }


    suspend fun updateAllTaskType(id: Int, taskType: TaskType): TaskType? {
        return performApiCall { RetrofitInstance.api.updateAllTaskType(id, taskType) }
    }

    suspend fun updateTaskType(id: Int, taskType: TaskType): TaskType? {
        return performApiCall { RetrofitInstance.api.updateTaskType(id, taskType) }
    }


    suspend fun deleteTaskType(id: Int): TaskType? {
        return performApiCall { RetrofitInstance.api.deleteTaskType(id) }
    }


    suspend fun getTeacher(id: Int): Teacher? {
        return performApiCall { RetrofitInstance.api.getTeacher(id) }
    }


    suspend fun getTeacherByEmail(email: String): Teacher? {
        return performApiCall { RetrofitInstance.api.getTeacherByEmail(email) }
    }


    suspend fun getTeacherByUserName(userName: String): Teacher? {
        return performApiCall { RetrofitInstance.api.getTeacherByUserName(userName) }
    }


    suspend fun getTeacherByLogin(userName: String, password: String): Teacher? {
        return performApiCall { RetrofitInstance.api.getTeacherByLogin(userName, password) }
    }


    suspend fun getAllTeachers(): List<Teacher>? {
        return performApiCall { RetrofitInstance.api.getAllTeachers() }
    }


    suspend fun createTeacher(teacher: Teacher): Teacher? {
        return performApiCall { RetrofitInstance.api.createTeacher(teacher) }
    }

    suspend fun updateAllTeacher(id: Int, teacher: Teacher): Teacher? {
        return performApiCall { RetrofitInstance.api.updateAllTeacher(id, teacher) }
    }

    suspend fun updateTeacher(id: Int, teacher: Teacher): Teacher? {
        return performApiCall { RetrofitInstance.api.updateTeacher(id, teacher) }
    }


    suspend fun deleteTeacher(id: Int): Teacher? {
        return performApiCall { RetrofitInstance.api.deleteTeacher(id) }
    }


    suspend fun getImage(id: Int): Image? {
        return performApiCall { RetrofitInstance.api.getImage(id) }
    }


    suspend fun getAllImages(): List<Image>? {
        return performApiCall { RetrofitInstance.api.getAllImages() }
    }


    suspend fun createImage(dbImage: Image): Image? {
        return performApiCall { RetrofitInstance.api.createImage(dbImage) }
    }


    suspend fun updateAllImage(id: Int, dbImage: Image): Image? {
        return performApiCall { RetrofitInstance.api.updateAllImage(id, dbImage) }
    }


    suspend fun deleteImage(id: Int): Image? {
        return performApiCall { RetrofitInstance.api.deleteImage(id) }
    }


    suspend fun getBoard(id: Int): Board?{
        return performApiCall { RetrofitInstance.api.getBoard(id) }
    }

    suspend fun getAllBoards(): List<Board>?{
        return performApiCall { RetrofitInstance.api.getAllBoards() }
    }

    suspend fun getTaskBoards(taskId: Int): List<Board>?{
        return performApiCall { RetrofitInstance.api.getTaskBoards(taskId) }
    }

    suspend fun createBoard(board: Board): Board?{
        board.tiles = null
        return performApiCall { RetrofitInstance.api.createBoard(board) }
    }

    suspend fun updateAllBoard(id: Int, board: Board): Board?{
        return performApiCall { RetrofitInstance.api.updateAllBoard(id, board) }
    }

    suspend fun deleteBoard(id: Int): Board?{
        return performApiCall { RetrofitInstance.api.deleteBoard(id) }
    }


    suspend fun getTile(id: Int): Tile?{
        return performApiCall { RetrofitInstance.api.getTile(id) }
    }

    suspend fun getAllTiles(): List<Tile>?{
        return performApiCall { RetrofitInstance.api.getAllTiles() }
    }

    suspend fun getBoardTiles(boardId: Int): List<Tile>?{
        return performApiCall { RetrofitInstance.api.getBoardTiles(boardId) }
    }

    suspend fun createTile(tile: Tile): Tile?{
        return performApiCall { RetrofitInstance.api.createTile(tile) }
    }

    suspend fun updateAllTile(id: Int, tile: Tile): Tile?{
        return performApiCall { RetrofitInstance.api.updateAllTile(id, tile) }
    }

    suspend fun deleteTile(id: Int): Tile?{
        return performApiCall { RetrofitInstance.api.deleteTile(id) }
    }


}