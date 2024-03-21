package com.example.eduenvi

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

    @POST("api/Classroom")
    suspend fun createClassroom(@Body classroom: Classroom): Response<Classroom>

    @PUT("api/Classroom/{id}")
    suspend fun updateClassroom(@Path("id") id: Int, @Body classroom: Classroom): Response<Classroom>

    @DELETE("api/Classroom/{id}")
    suspend fun deleteClassroom(@Path("id") id: Int): Response<Void>


    @GET("api/ClassroomTask/{classroomId}/{taskId}")
    suspend fun getClassroomTask(@Path("classroomId") classroomId: Int, @Path("taskId") taskId: Int): Response<ClassroomTask>

    @POST("api/ClassroomTask")
    suspend fun createClassroomTask(@Body classroomTask: ClassroomTask): Response<ClassroomTask>

    @PUT("api/ClassroomTask/{classroomId}/{taskId}")
    suspend fun updateClassroomTask(@Path("classroomId") classroomId: Int, @Path("taskId") taskId: Int, @Body classroomTask: ClassroomTask): Response<ClassroomTask>

    @DELETE("api/ClassroomTask/{classroomId}/{taskId}")
    suspend fun deleteClassroomTask(@Path("classroomId") classroomId: Int, @Path("taskId") taskId: Int): Response<Void>


    @GET("api/Group/{id}")
    suspend fun getGroup(@Path("id") id: Int): Response<Group>

    @GET("api/Group")
    suspend fun getAllGroups(): Response<List<Group>>

    @GET("api/Group/getByClassroomId/{classroomId}")
    suspend fun getGroupsInClassroom(@Path("classroomId") classroomId: Int): Response<List<Group>>

    @GET("api/Group/getByStudentId/{id}")
    suspend fun getStudentsGroups(@Path("id") id: Int): Response<List<Group>>

    @GET("api/Group/getByClassroomIdNotInStudentId/{classroomId}/{studentId}")
    suspend fun getGroupsFromInClassroomNotInStudent(@Path("classroomId") classroomId: Int, @Path("studentId") studentId: Int,): Response<List<Group>>

    @POST("api/Group")
    suspend fun createGroup(@Body group: Group): Response<Group>

    @PUT("api/Group/{id}")
    suspend fun updateGroup(@Path("id") id: Int, @Body group: Group): Response<Group>

    @DELETE("api/Group/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Void>


    @GET("api/GroupTask/{groupId}/{taskId}")
    suspend fun getGroupTask(@Path("groupId") groupId: Int, @Path("taskId") taskId: Int): Response<GroupTask>

    @POST("api/GroupTask")
    suspend fun createGroupTask(@Body groupTask: GroupTask): Response<GroupTask>

    @PUT("api/GroupTask/{groupId}/{taskId}")
    suspend fun updateGroupTask(@Path("groupId") groupId: Int, @Path("taskId") taskId: Int, @Body groupTask: GroupTask): Response<GroupTask>

    @DELETE("api/GroupTask/{groupId}/{taskId}")
    suspend fun deleteGroupTask(@Path("groupId") groupId: Int, @Path("taskId") taskId: Int): Response<Void>


    @GET("api/Student/{id}")
    suspend fun getStudent(@Path("id") id: Int): Response<Student>

    @GET("api/Student/getByLogin/{loginCode}")
    suspend fun getStudentByLoginCode(@Path("loginCode") loginCode: String): Response<Student>

    @GET("api/Student")
    suspend fun getAllStudents(): Response<List<Student>>

    @GET("api/Student/getByGroupId/{groupId}")
    suspend fun getStudentsInGroup(@Path("groupId") groupId: Int): Response<List<Student>>

    @GET("api/Student/getByClassroomIdNotInGroupId/{classroomId}/{groupId}")
    suspend fun getStudentsFromClassroomNotInGroup(@Path("classroomId") classroomId: Int, @Path("groupId") groupId: Int): Response<List<Student>>

    @GET("api/Student/getByClassroomId/{classroomId}")
    suspend fun getStudentsInClassroom(@Path("classroomId") classroomId: Int): Response<List<Student>>

    @POST("api/Student")
    suspend fun createStudent(@Body student: Student): Response<Student>

    @PUT("api/Student/{id}")
    suspend fun updateStudent(@Path("id") id: Int, @Body student: Student): Response<Student>

    @DELETE("api/Student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int): Response<Void>


    @GET("api/StudentGroup/{studentId}/{groupId}")
    suspend fun getStudentGroup(@Path("studentId") studentId: Int, @Path("groupId") groupId: Int): Response<StudentGroup>

    @POST("api/StudentGroup")
    suspend fun createStudentGroup(@Body studentgroup: StudentGroup): Response<StudentGroup>

    @PUT("api/StudentGroup/{studentId}/{groupId}")
    suspend fun updateStudentGroup(@Path("studentId") studentId: Int, @Path("groupId") groupId: Int, @Body studentgroup: StudentGroup): Response<StudentGroup>

    @DELETE("api/StudentGroup/{studentId}/{groupId}")
    suspend fun deleteStudentGroup(@Path("studentId") studentId: Int, @Path("groupId") groupId: Int): Response<Void>


    @GET("api/StudentTask/{studentId}/{taskId}")
    suspend fun getStudentTask(@Path("studentId") studentId: Int, @Path("taskId") taskId: Int): Response<StudentTask>

    @POST("api/StudentTask")
    suspend fun createStudentTask(@Body studentTask: StudentTask): Response<StudentTask>

    @PUT("api/StudentTask/{studentId}/{taskId}")
    suspend fun updateStudentTask(@Path("studentId") studentId: Int, @Path("taskId") taskId: Int, @Body studentTask: StudentTask): Response<StudentTask>

    @DELETE("api/StudentTask/{{studentId}/{taskId}")
    suspend fun deleteStudentTask(@Path("studentId") studentId: Int, @Path("taskId") taskId: Int): Response<Void>


    @GET("api/Task/{id}")
    suspend fun getTask(@Path("id") id: Int): Response<Task>

    @GET("api/Task/getByClassroomId/{classroomId}")
    suspend fun getTasksInClassroom(@Path("classroomId") classroomId: Int): Response<List<Task>>

    @GET("api/Task/getByGroupId/{groupId}")
    suspend fun getGroupsTasks(@Path("groupId") groupId: Int): Response<List<Task>>

    @GET("api/Task/getByStudentId/{studentId}")
    suspend fun getStudentsTasks(@Path("studentId") studentId: Int): Response<List<Task>>

    @GET("api/Task/getByTeacherIdNotInGroupId/{teacherId}/{groupId}")
    suspend fun getTasksFromTeacherNotInGroup(@Path("teacherId") teacherId: Int, @Path("groupId") groupId: Int): Response<List<Task>>

    @GET("api/Task/getByTeacherIdNotInStudentId/{teacherId}/{studentId}")
    suspend fun getTasksFromTeacherNotInStudent(@Path("teacherId") teacherId: Int, @Path("studentId") studentId: Int): Response<List<Task>>

    @POST("api/Task")
    suspend fun createTask(@Body task: Task): Response<Task>

    @PUT("api/Task/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: Task): Response<Task>

    @DELETE("api/Task/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Void>


    @GET("api/TaskType")
    suspend fun getAllTaskTypes(): Response<List<TaskType>>

    @POST("api/TaskType")
    suspend fun createTaskType(@Body taskType: TaskType): Response<TaskType>

    @PUT("api/TaskType/{id}")
    suspend fun updateTaskType(@Path("id") id: Int, @Body taskType: TaskType): Response<TaskType>

    @DELETE("api/TaskType/{id}")
    suspend fun deleteTaskType(@Path("id") id: Int): Response<Void>


    @GET("api/Teacher/{id}")
    suspend fun getTeacher(@Path("id") id: Int): Response<Teacher>

    @GET("api/Teacher/getByEmail/{email}")
    suspend fun getTeacherByEmail(@Path("email") email: String): Response<Teacher>

    @GET("api/Teacher/getByUserName/{userName}")
    suspend fun getTeacherByUserName(@Path("userName") userName: String): Response<Teacher>

    @GET("api/Teacher/getByLogin/{userName}/{password}")
    suspend fun getTeacherByLogin(@Path("userName") userName: String, @Path("password") password: String): Response<Teacher>

    @GET("api/Teacher")
    suspend fun getAllTeachers(): Response<List<Teacher>>

    @POST("api/Teacher")
    suspend fun createTeacher(@Body teacher: Teacher): Response<Teacher>

    @PUT("api/Teacher/{id}")
    suspend fun updateTeacher(@Path("id") id: Int, @Body teacher: Teacher): Response<Teacher>

    @DELETE("api/Teacher/{id}")
    suspend fun deleteTeacher(@Path("id") id: Int): Response<Void>


    @GET("api/Edge/{id}")
    suspend fun getEdge(@Path("id") id: Int): Response<Edge>

    @GET("api/Edge")
    suspend fun getAllEdges(): Response<List<Edge>>

    @GET("api/Edge/getByFromVertexId/{fromVertexId}")
    suspend fun getFromVertexEdges(@Path("fromVertexId") fromVertexId: Int): Response<List<Edge>>

    @POST("api/Edge")
    suspend fun createEdge(@Body edge: Edge): Response<Edge>

    @PUT("api/Edge/{id}")
    suspend fun updateEdge(@Path("id") id: Int, @Body edge: Edge): Response<Edge>

    @DELETE("api/Edge/{id}")
    suspend fun deleteEdge(@Path("id") id: Int): Response<Void>


    @GET("api/Vertex/{id}")
    suspend fun geVertex(@Path("id") id: Int): Response<Vertex>

    @GET("api/Vertex")
    suspend fun getAlVertices(): Response<List<Vertex>>

    @GET("api/Vertex/getByTaskId/{taskId}")
    suspend fun getTaskVertices(@Path("taskId") taskId: Int): Response<List<Vertex>>

    @POST("api/Vertex")
    suspend fun createVertex(@Body vertex: Vertex): Response<Vertex>

    @PUT("api/Vertex/{id}")
    suspend fun updateVertex(@Path("id") id: Int, @Body vertex: Vertex): Response<Vertex>

    @DELETE("api/Vertex/{id}")
    suspend fun deleteVertex(@Path("id") id: Int): Response<Void>


    @GET("api/DBImage/{id}")
    suspend fun geDBImage(@Path("id") id: Int): Response<DBImage>

    @GET("api/DBImage")
    suspend fun getAlImages(): Response<List<DBImage>>

    @POST("api/DBImage")
    suspend fun createDBImage(@Body dbImage: DBImage): Response<DBImage>

    @PUT("api/DBImage/{id}")
    suspend fun updateDBImage(@Path("id") id: Int, @Body dbImage: DBImage): Response<DBImage>

    @DELETE("api/DBImage/{id}")
    suspend fun deleteDBImage(@Path("id") id: Int): Response<Void>

}