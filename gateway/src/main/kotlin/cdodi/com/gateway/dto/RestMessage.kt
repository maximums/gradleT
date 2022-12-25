package cdodi.com.gateway.dto

@kotlinx.serialization.Serializable
data class RestMessageRequest(
    val content: String,
    val receiverId: Long,
    val isRead: Boolean,
)

@kotlinx.serialization.Serializable
data class RestMessageResponse(
    val id: Long,
    val content: String,
    val receiverId: Long,
    val timestamp: Long,
    val isRead: Boolean,
)

@kotlinx.serialization.Serializable
data class RestEditMessage(
    val id: Long,
    val content: String
)
