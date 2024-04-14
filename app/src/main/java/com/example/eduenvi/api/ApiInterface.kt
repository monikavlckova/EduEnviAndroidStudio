package com.example.eduenvi.api

import com.example.eduenvi.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("api/Classroom/{id}")
    suspend fun getClassroom(@Path("id") id: Int): Response<Classroom>

    @GET("api/Classroom")
    suspend fun getAllClassrooms(): Response<List<Classroom>>

    @GET("api/Classroom/getByTeacherId/{teacherId}")
    suspend fun getTeachersClassroom(@Path("teacherId") teacherId: Int): Response<List<Classroom>>

    @PUT("api/Classroom")
    suspend fun createClassroom(@Body classroom: Classroom): Response<Classroom>

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


    @GET("api/ClassroomTask/{classroomId}/{taskId}")
    suspend fun getClassroomTask(
        @Path("classroomId") classroomId: Int,
        @Path("taskId") taskId: Int
    ): Response<ClassroomTask>

    @PUT("api/ClassroomTask")
    suspend fun createClassroomTask(@Body classroomTask: ClassroomTask): Response<ClassroomTask>

    @POST("api/ClassroomTask/{classroomId}/{taskId}")
    suspend fun updateAllClassroomTask(
        @Path("classroomId") classroomId: Int,
        @Path("taskId") taskId: Int,
        @Body classroomTask: ClassroomTask
    ): Response<ClassroomTask>

    @DELETE("api/ClassroomTask/{classroomId}/{taskId}")
    suspend fun deleteClassroomTask(
        @Path("classroomId") classroomId: Int,
        @Path("taskId") taskId: Int
    ): Response<ClassroomTask>


    @GET("api/Group/{id}")
    suspend fun getGroup(@Path("id") id: Int): Response<Group>

    @GET("api/Group")
    suspend fun getAllGroups(): Response<List<Group>>

    @GET("api/Group/getByClassroomId/{classroomId}")
    suspend fun getGroupsInClassroom(@Path("classroomId") classroomId: Int): Response<List<Group>>

    @GET("api/Group/getByStudentId/{id}")
    suspend fun getStudentsGroups(@Path("id") id: Int): Response<List<Group>>

    @GET("api/Group/getByClassroomIdNotInStudentId/{classroomId}/{studentId}")
    suspend fun getGroupsFromInClassroomNotInStudent(
        @Path("classroomId") classroomId: Int,
        @Path("studentId") studentId: Int,
    ): Response<List<Group>>

    @PUT("api/Group")
    suspend fun createGroup(@Body group: Group): Response<Group>

    @POST("api/Group/{id}")
    suspend fun updateAllGroup(@Path("id") id: Int, @Body group: Group): Response<Group>

    @PATCH("api/Group/{id}")
    suspend fun updateGroup(@Path("id") id: Int, @Body group: Group): Response<Group>

    @DELETE("api/Group/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Group>


    @GET("api/GroupTask/{groupId}/{taskId}")
    suspend fun getGroupTask(
        @Path("groupId") groupId: Int,
        @Path("taskId") taskId: Int
    ): Response<GroupTask>

    @PUT("api/GroupTask")
    suspend fun createGroupTask(@Body groupTask: GroupTask): Response<GroupTask>

    @POST("api/GroupTask/{groupId}/{taskId}")
    suspend fun updateAllGroupTask(
        @Path("groupId") groupId: Int,
        @Path("taskId") taskId: Int,
        @Body groupTask: GroupTask
    ): Response<GroupTask>

    @DELETE("api/GroupTask/{groupId}/{taskId}")
    suspend fun deleteGroupTask(
        @Path("groupId") groupId: Int,
        @Path("taskId") taskId: Int
    ): Response<GroupTask>


    @GET("api/Student/{id}")
    suspend fun getStudent(@Path("id") id: Int): Response<Student>

    @GET("api/Student/getByLogin/{loginCode}")
    suspend fun getStudentByLoginCode(@Path("loginCode") loginCode: String): Response<Student>

    @GET("api/Student")
    suspend fun getAllStudents(): Response<List<Student>>

    @GET("api/Student/getByGroupId/{groupId}")
    suspend fun getStudentsInGroup(@Path("groupId") groupId: Int): Response<List<Student>>

    @GET("api/Student/getByClassroomIdNotInGroupId/{classroomId}/{groupId}")
    suspend fun getStudentsFromClassroomNotInGroup(
        @Path("classroomId") classroomId: Int,
        @Path("groupId") groupId: Int
    ): Response<List<Student>>

    @GET("api/Student/getByClassroomId/{classroomId}")
    suspend fun getStudentsInClassroom(@Path("classroomId") classroomId: Int): Response<List<Student>>

    @PUT("api/Student")
    suspend fun createStudent(@Body student: Student): Response<Student>

    @POST("api/Student/{id}")
    suspend fun updateAllStudent(@Path("id") id: Int, @Body student: Student): Response<Student>

    @PATCH("api/Student/{id}")
    suspend fun updateStudent(@Path("id") id: Int, @Body student: Student): Response<Student>

    @DELETE("api/Student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int): Response<Student>


    @GET("api/StudentGroup/{studentId}/{groupId}")
    suspend fun getStudentGroup(
        @Path("studentId") studentId: Int,
        @Path("groupId") groupId: Int
    ): Response<StudentGroup>

    @PUT("api/StudentGroup")
    suspend fun createStudentGroup(@Body studentgroup: StudentGroup): Response<StudentGroup>

    @POST("api/StudentGroup/{studentId}/{groupId}")
    suspend fun updateAllStudentGroup(
        @Path("studentId") studentId: Int,
        @Path("groupId") groupId: Int,
        @Body studentgroup: StudentGroup
    ): Response<StudentGroup>

    @DELETE("api/StudentGroup/{studentId}/{groupId}")
    suspend fun deleteStudentGroup(
        @Path("studentId") studentId: Int,
        @Path("groupId") groupId: Int
    ): Response<StudentGroup>


    @GET("api/StudentTask/{studentId}/{taskId}")
    suspend fun getStudentTask(
        @Path("studentId") studentId: Int,
        @Path("taskId") taskId: Int
    ): Response<StudentTask>

    @PUT("api/StudentTask")
    suspend fun createStudentTask(@Body studentTask: StudentTask): Response<StudentTask>

    @POST("api/StudentTask/{studentId}/{taskId}")
    suspend fun updateAllStudentTask(
        @Path("studentId") studentId: Int,
        @Path("taskId") taskId: Int,
        @Body studentTask: StudentTask
    ): Response<StudentTask>

    @DELETE("api/StudentTask/{{studentId}/{taskId}")
    suspend fun deleteStudentTask(
        @Path("studentId") studentId: Int,
        @Path("taskId") taskId: Int
    ): Response<StudentTask>


    @GET("api/Task/{id}")
    suspend fun getTask(@Path("id") id: Int): Response<Task>

    @GET("api/Task/getByClassroomId/{classroomId}")
    suspend fun getTasksInClassroom(@Path("classroomId") classroomId: Int): Response<List<Task>>

    @GET("api/Task/getByGroupId/{groupId}")
    suspend fun getGroupsTasks(@Path("groupId") groupId: Int): Response<List<Task>>

    @GET("api/Task/getByStudentId/{studentId}")
    suspend fun getStudentsTasks(@Path("studentId") studentId: Int): Response<List<Task>>

    @GET("api/Task/getByTeacherIdNotInGroupId/{teacherId}/{groupId}")
    suspend fun getTasksFromTeacherNotInGroup(
        @Path("teacherId") teacherId: Int,
        @Path("groupId") groupId: Int
    ): Response<List<Task>>

    @GET("api/Task/getByTeacherIdNotInStudentId/{teacherId}/{studentId}")
    suspend fun getTasksFromTeacherNotInStudent(
        @Path("teacherId") teacherId: Int,
        @Path("studentId") studentId: Int
    ): Response<List<Task>>

    @GET("api/Task/getByStudentIdInAllGroups/{studentId}")
    suspend fun getTasksInStudentsGroups(@Path("studentId") studentId: Int): Response<List<Task>>

    @PUT("api/Task")
    suspend fun createTask(@Body task: Task): Response<Task>

    @POST("api/Task/{id}")
    suspend fun updateAllTask(@Path("id") id: Int, @Body task: Task): Response<Task>

    @PATCH("api/Task/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: Task): Response<Task>

    @DELETE("api/Task/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Task>


    @GET("api/TaskType")
    suspend fun getAllTaskTypes(): Response<List<TaskType>>

    @PUT("api/TaskType")
    suspend fun createTaskType(@Body taskType: TaskType): Response<TaskType>

    @POST("api/TaskType/{id}")
    suspend fun updateAllTaskType(@Path("id") id: Int, @Body taskType: TaskType): Response<TaskType>

    @PATCH("api/TaskType/{id}")
    suspend fun updateTaskType(@Path("id") id: Int, @Body taskType: TaskType): Response<TaskType>

    @DELETE("api/TaskType/{id}")
    suspend fun deleteTaskType(@Path("id") id: Int): Response<TaskType>


    @GET("api/Teacher/{id}")
    suspend fun getTeacher(@Path("id") id: Int): Response<Teacher>

    @GET("api/Teacher/getByEmail/{email}")
    suspend fun getTeacherByEmail(@Path("email") email: String): Response<Teacher>

    @GET("api/Teacher/getByUserName/{userName}")
    suspend fun getTeacherByUserName(@Path("userName") userName: String): Response<Teacher>

    @GET("api/Teacher/getByLogin/{userName}/{password}")
    suspend fun getTeacherByLogin(
        @Path("userName") userName: String,
        @Path("password") password: String
    ): Response<Teacher>

    @GET("api/Teacher")
    suspend fun getAllTeachers(): Response<List<Teacher>>

    @PUT("api/Teacher")
    suspend fun createTeacher(@Body teacher: Teacher): Response<Teacher>

    @POST("api/Teacher/{id}")
    suspend fun updateAllTeacher(@Path("id") id: Int, @Body teacher: Teacher): Response<Teacher>

    @PATCH("api/Teacher/{id}")
    suspend fun updateTeacher(@Path("id") id: Int, @Body teacher: Teacher): Response<Teacher>

    @DELETE("api/Teacher/{id}")
    suspend fun deleteTeacher(@Path("id") id: Int): Response<Teacher>


    @GET("api/Edge/{id}")
    suspend fun getEdge(@Path("id") id: Int): Response<Edge>

    @GET("api/Edge")
    suspend fun getAllEdges(): Response<List<Edge>>

    @GET("api/Edge/getByFromVertexId/{fromVertexId}")
    suspend fun getFromVertexEdges(@Path("fromVertexId") fromVertexId: Int): Response<List<Edge>>

    @PUT("api/Edge")
    suspend fun createEdge(@Body edge: Edge): Response<Edge>

    @POST("api/Edge/{id}")
    suspend fun updateAllEdge(@Path("id") id: Int, @Body edge: Edge): Response<Edge>

    @PATCH("api/Edge/{id}")
    suspend fun updateEdge(@Path("id") id: Int, @Body edge: Edge): Response<Edge>

    @DELETE("api/Edge/{id}")
    suspend fun deleteEdge(@Path("id") id: Int): Response<Edge>


    @GET("api/Vertex/{id}")
    suspend fun geVertex(@Path("id") id: Int): Response<Vertex>

    @GET("api/Vertex")
    suspend fun getAlVertices(): Response<List<Vertex>>

    @GET("api/Vertex/getByTaskId/{taskId}")
    suspend fun getTaskVertices(@Path("taskId") taskId: Int): Response<List<Vertex>>

    @PUT("api/Vertex")
    suspend fun createVertex(@Body vertex: Vertex): Response<Vertex>

    @POST("api/Vertex/{id}")
    suspend fun updateAllVertex(@Path("id") id: Int, @Body vertex: Vertex): Response<Vertex>

    @PATCH("api/Vertex/{id}")
    suspend fun updateVertex(@Path("id") id: Int, @Body vertex: Vertex): Response<Vertex>

    @DELETE("api/Vertex/{id}")
    suspend fun deleteVertex(@Path("id") id: Int): Response<Vertex>


    @GET("api/Image/{id}")
    suspend fun getImage(@Path("id") id: Int): Response<Image>

    @GET("api/Image")
    suspend fun getAllImages(): Response<List<Image>>

    @PUT("api/Image")
    suspend fun createImage(@Body dbImage: Image): Response<Image>

    @POST("api/Image/{id}")
    suspend fun updateAllImage(@Path("id") id: Int, @Body dbImage: Image): Response<Image>

    @DELETE("api/Image/{id}")
    suspend fun deleteImage(@Path("id") id: Int): Response<Image>

}