package com.example.eduenvi.api

import com.example.eduenvi.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("api/AssignedTask")
    suspend fun getAllAssignedTasks(): Response<List<AssignedTask>>

    @PUT("api/AssignedTask")
    suspend fun createAssignedTask(@Body assignedTask: AssignedTask): Response<AssignedTask>

    @POST("api/AssignedTask/{id}")
    suspend fun updateAllAssignedTask(
        @Path("id") id: Int,
        @Body assignedTask: AssignedTask
    ): Response<AssignedTask>

    @DELETE("api/AssignedTask/{id}")
    suspend fun deleteAssignedTask(@Path("id") id: Int): Response<AssignedTask>


    @GET("api/Board")
    suspend fun getAllBoards(): Response<List<Board>>

    @PUT("api/Board")
    suspend fun createBoard(@Body board: Board): Response<Board>

    @GET("api/Board/{id}")
    suspend fun getBoard(@Path("id") id: Int): Response<Board>

    @POST("api/Board/{id}")
    suspend fun updateAllBoard(@Path("id") id: Int, @Body board: Board): Response<Board>

    @DELETE("api/Board/{id}")
    suspend fun deleteBoard(@Path("id") id: Int): Response<Board>

    @GET("api/Board/getByTaskId/{taskId}")
    suspend fun getTaskBoards(@Path("taskId") taskId: Int): Response<List<Board>>


    @GET("api/Classroom")
    suspend fun getAllClassrooms(): Response<List<Classroom>>

    @PUT("api/Classroom")
    suspend fun createClassroom(@Body classroom: Classroom): Response<Classroom>

    @GET("api/Classroom/{id}")
    suspend fun getClassroom(@Path("id") id: Int): Response<Classroom>

    @POST("api/Classroom/{id}")
    suspend fun updateAllClassroom(
        @Path("id") id: Int,
        @Body classroom: Classroom
    ): Response<Classroom>

    @PATCH("api/Classroom/{id}")
    suspend fun updateClassroom(
        @Path("id") id: Int,
        @Body classroom: Classroom
    ): Response<Classroom>

    @DELETE("api/Classroom/{id}")
    suspend fun deleteClassroom(@Path("id") id: Int): Response<Classroom>

    @GET("api/Classroom/getByTeacherId/{teacherId}")
    suspend fun getTeachersClassroom(@Path("teacherId") teacherId: Int): Response<List<Classroom>>


    @GET("api/Group")
    suspend fun getAllGroups(): Response<List<Group>>

    @PUT("api/Group")
    suspend fun createGroup(@Body group: Group): Response<Group>

    @GET("api/Group/{id}")
    suspend fun getGroup(@Path("id") id: Int): Response<Group>

    @POST("api/Group/{id}")
    suspend fun updateAllGroup(@Path("id") id: Int, @Body group: Group): Response<Group>

    @PATCH("api/Group/{id}")
    suspend fun updateGroup(@Path("id") id: Int, @Body group: Group): Response<Group>

    @DELETE("api/Group/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Group>

    @GET("api/Group/getByAssignedTaskId/{assignedTaskId}")
    suspend fun getGroupsInAssignedTask(@Path("assignedTaskId") assignedTaskId: Int): Response<List<Group>>

    @GET("api/Group/getByClassroomId/{classroomId}")
    suspend fun getGroupsInClassroom(@Path("classroomId") classroomId: Int): Response<List<Group>>

    @GET("api/Group/getByStudentId/{id}")
    suspend fun getStudentsGroups(@Path("id") id: Int): Response<List<Group>>

    @GET("api/Group/getByClassroomIdNotInStudentId/{classroomId}/{studentId}")
    suspend fun getGroupsFromClassroomNotInStudent(
        @Path("classroomId") classroomId: Int,
        @Path("studentId") studentId: Int,
    ): Response<List<Group>>

    @GET("api/Group/getByClassroomIdNotInAssignedTaskId/{classroomId}/{assignedTaskId}")
    suspend fun getGroupsFromClassroomNotInAssignedTask(
        @Path("classroomId") classroomId: Int,
        @Path("assignedTaskId") assignedTaskId: Int
    ): Response<List<Group>>


    @GET("api/GroupAssignedTask")
    suspend fun getAllGroupAssignedTasks(): Response<List<GroupAssignedTask>>

    @PUT("api/GroupAssignedTask")
    suspend fun createGroupAssignedTask(@Body groupAssignedTask: GroupAssignedTask): Response<GroupAssignedTask>

    @DELETE("api/GroupAssignedTask/{groupId}/{assignedTaskId}")
    suspend fun deleteGroupAssignedTask(
        @Path("groupId") groupId: Int,
        @Path("assignedTaskId") assignedTaskId: Int
    ): Response<GroupAssignedTask>

    @GET("api/Image")
    suspend fun getAllImages(): Response<List<Image>>

    @PUT("api/Image")
    suspend fun createImage(@Body dbImage: Image): Response<Image>

    @GET("api/Image/{id}")
    suspend fun getImage(@Path("id") id: Int): Response<Image>

    @POST("api/Image/{id}")
    suspend fun updateAllImage(@Path("id") id: Int, @Body dbImage: Image): Response<Image>

    @DELETE("api/Image/{id}")
    suspend fun deleteImage(@Path("id") id: Int): Response<Image>


    @GET("api/Student")
    suspend fun getAllStudents(): Response<List<Student>>

    @PUT("api/Student")
    suspend fun createStudent(@Body student: Student): Response<Student>

    @GET("api/Student/{id}")
    suspend fun getStudent(@Path("id") id: Int): Response<Student>

    @POST("api/Student/{id}")
    suspend fun updateAllStudent(@Path("id") id: Int, @Body student: Student): Response<Student>

    @PATCH("api/Student/{id}")
    suspend fun updateStudent(@Path("id") id: Int, @Body student: Student): Response<Student>

    @DELETE("api/Student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int): Response<Student>

    @GET("api/Student/getByAssignedTaskId/{assignedTaskId}")
    suspend fun getStudentsInAssignedTask(@Path("assignedTaskId") assignedTaskId: Int): Response<List<Student>>

    @GET("api/Student/getByLogin/{loginCode}")
    suspend fun getStudentByLoginCode(@Path("loginCode") loginCode: String): Response<Student>

    @GET("api/Student/getByClassroomId/{classroomId}")
    suspend fun getStudentsInClassroom(@Path("classroomId") classroomId: Int): Response<List<Student>>

    @GET("api/Student/getByGroupId/{groupId}")
    suspend fun getStudentsInGroup(@Path("groupId") groupId: Int): Response<List<Student>>

    @GET("api/Student/getByClassroomIdNotInGroupId/{classroomId}/{groupId}")
    suspend fun getStudentsFromClassroomNotInGroup(
        @Path("classroomId") classroomId: Int,
        @Path("groupId") groupId: Int
    ): Response<List<Student>>

    @GET("api/Student/getByClassroomIdNotInAssignedTaskId/{classroomId}/{assignedTaskId}")
    suspend fun getStudentsFromClassroomNotInAssignedTask(
        @Path("classroomId") classroomId: Int,
        @Path("assignedTaskId") assignedTaskId: Int
    ): Response<List<Student>>


    @GET("api/StudentAssignedTask/")
    suspend fun getAllStudentAssignedTask(): Response<List<StudentAssignedTask>>

    @PUT("api/StudentAssignedTask")
    suspend fun createStudentAssignedTask(@Body studentAssignedTask: StudentAssignedTask): Response<StudentAssignedTask>

    @DELETE("api/StudentAssignedTask/{studentId}/{assignedTaskId}")
    suspend fun deleteStudentAssignedTask(
        @Path("studentId") studentId: Int,
        @Path("assignedTaskId") assignedTaskId: Int
    ): Response<StudentAssignedTask>


    @PUT("api/StudentGroup")
    suspend fun createStudentGroup(@Body studentgroup: StudentGroup): Response<StudentGroup>

    @GET("api/StudentGroup/getByStudentId/{studentId}")
    suspend fun getStudentGroupsByStudentId(@Path("studentId") studentId: Int): Response<List<StudentGroup>>

    @GET("api/StudentGroup/getByGroupId/{groupId}")
    suspend fun getStudentGroupsByGroupId(@Path("groupId") groupId: Int): Response<List<StudentGroup>>

    @DELETE("api/StudentGroup/{studentId}/{groupId}")
    suspend fun deleteStudentGroup(
        @Path("studentId") studentId: Int,
        @Path("groupId") groupId: Int
    ): Response<StudentGroup>


    @PUT("api/Task")
    suspend fun createTask(@Body task: Task): Response<Task>

    @GET("api/Task/{id}")
    suspend fun getTask(@Path("id") id: Int): Response<Task>

    @POST("api/Task/{id}")
    suspend fun updateAllTask(@Path("id") id: Int, @Body task: Task): Response<Task>

    @PATCH("api/Task/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: Task): Response<Task>

    @DELETE("api/Task/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Task>

    @GET("api/Task/getByTeacherId/{teacherId}")
    suspend fun getTeacherTasks(@Path("teacherId") teacherId: Int): Response<List<Task>>

    @GET("api/Task/getAssignedByTeacherId/{teacherId}")
    suspend fun getTeacherAssignedTasks(@Path("teacherId") teacherId: Int): Response<List<Task>>

    @GET("api/Task/getAllByTeacherId/{teacherId}")
    suspend fun getTeachersTasksAndAssignedTasks(@Path("teacherId") teacherId: Int): Response<List<Task>>

    @GET("api/Task/getByClassroomId/{classroomId}")
    suspend fun getTasksInClassroom(@Path("classroomId") classroomId: Int): Response<List<Task>>

    @GET("api/Task/getByGroupId/{groupId}")
    suspend fun getGroupsTasks(@Path("groupId") groupId: Int): Response<List<Task>>

    @GET("api/Task/getByStudentId/{studentId}")
    suspend fun getStudentsTasks(@Path("studentId") studentId: Int): Response<List<Task>>

    @GET("api/Task/getByStudentIdInAllGroups/{studentId}")
    suspend fun getTasksInStudentsGroups(@Path("studentId") studentId: Int): Response<List<Task>>


    @GET("api/TaskType")
    suspend fun getAllTaskTypes(): Response<List<TaskType>>

    @PUT("api/TaskType")
    suspend fun createTaskType(@Body taskType: TaskType): Response<TaskType>

    @GET("api/TaskType/{id}")
    suspend fun getTaskType(@Path("id") id: Int): Response<TaskType>

    @POST("api/TaskType/{id}")
    suspend fun updateAllTaskType(@Path("id") id: Int, @Body taskType: TaskType): Response<TaskType>

    @PATCH("api/TaskType/{id}")
    suspend fun updateTaskType(@Path("id") id: Int, @Body taskType: TaskType): Response<TaskType>

    @DELETE("api/TaskType/{id}")
    suspend fun deleteTaskType(@Path("id") id: Int): Response<TaskType>


    @GET("api/Teacher")
    suspend fun getAllTeachers(): Response<List<Teacher>>

    @PUT("api/Teacher")
    suspend fun createTeacher(@Body teacher: Teacher): Response<Teacher>

    @GET("api/Teacher/{id}")
    suspend fun getTeacher(@Path("id") id: Int): Response<Teacher>

    @POST("api/Teacher/{id}")
    suspend fun updateAllTeacher(@Path("id") id: Int, @Body teacher: Teacher): Response<Teacher>

    @PATCH("api/Teacher/{id}")
    suspend fun updateTeacher(@Path("id") id: Int, @Body teacher: Teacher): Response<Teacher>

    @DELETE("api/Teacher/{id}")
    suspend fun deleteTeacher(@Path("id") id: Int): Response<Teacher>

    @GET("api/Teacher/getByEmail/{email}")
    suspend fun getTeacherByEmail(@Path("email") email: String): Response<Teacher>

    @GET("api/Teacher/getByUserName/{userName}")
    suspend fun getTeacherByUserName(@Path("userName") userName: String): Response<Teacher>

    @GET("api/Teacher/getByLogin/{userName}/{password}")
    suspend fun getTeacherByLogin(
        @Path("userName") userName: String,
        @Path("password") password: String
    ): Response<Teacher>


    @GET("api/Tile")
    suspend fun getAllTiles(): Response<List<Tile>>

    @PUT("api/Tile")
    suspend fun createTile(@Body tile: Tile): Response<Tile>

    @GET("api/Tile/{id}")
    suspend fun getTile(@Path("id") id: Int): Response<Tile>

    @POST("api/Tile/{id}")
    suspend fun updateAllTile(@Path("id") id: Int, @Body tile: Tile): Response<Tile>

    @DELETE("api/Tile/{id}")
    suspend fun deleteTile(@Path("id") id: Int): Response<Tile>

    @GET("api/Tile/getByBoardId/{boardId}")
    suspend fun getBoardTiles(@Path("boardId") boardId: Int): Response<List<Tile>>
}