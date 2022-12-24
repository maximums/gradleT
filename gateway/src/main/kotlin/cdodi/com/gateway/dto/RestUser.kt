package cdodi.com.gateway.dto

@kotlinx.serialization.Serializable
data class RestUserRequest(
    val name: String?,
    val email: String,
    val password: String,
    val avatarId: Int,
    val dateOfBirth: Long,
    val hobbiesIds: String,
)

@kotlinx.serialization.Serializable
data class RestUserResponse(
    val id: Long,
    val name: String?,
    val email: String,
    val password: String,
    val avatarId: Int,
    val dateOfBirth: Long,
    val accountCreationTime: Long,
    val hobbiesIds: String,
)
