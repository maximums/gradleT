package cdodi.com.data.model

import com.cdodi.data.MessageResponse
import com.cdodi.data.Message as MessageRpc
import com.cdodi.data.User as UserRpc
import com.cdodi.data.UserResponse

fun User?.toUserResponse(): UserResponse =
    if (this == null) UserResponse.newBuilder().setIsNull(true).build()
    else UserResponse.newBuilder().setUser(
        User {
            id = this@toUserResponse.id.value
            name = this@toUserResponse.name
            email = this@toUserResponse.email
            password = this@toUserResponse.password
            avatarId = this@toUserResponse.avatarID
            dateOfBirth = this@toUserResponse.dateOfBirth
            hobbiesIds = this@toUserResponse.hobbiesIds
            accountCreationTime = this@toUserResponse.accountCreationTime
        }
    ).build()

fun User.toUserRpc(): UserRpc =
    User {
        id = this@toUserRpc.id.value
        name = this@toUserRpc.name
        email = this@toUserRpc.email
        password = this@toUserRpc.password
        avatarId = this@toUserRpc.avatarID
        dateOfBirth = this@toUserRpc.dateOfBirth
        hobbiesIds = this@toUserRpc.hobbiesIds
        accountCreationTime = this@toUserRpc.accountCreationTime
    }

inline fun User(builder: UserRpc.Builder.() -> Unit): UserRpc =
    UserRpc.newBuilder().apply(builder).build()


fun Message?.toMessageResponse(): MessageResponse =
    if (this == null) MessageResponse.newBuilder().setIsNull(true).build()
    else MessageResponse.newBuilder().setMessage(
        Message {
            id = this@toMessageResponse.id.value
            content = body
            senderId = this@toMessageResponse.sender
            receiverId = this@toMessageResponse.receiver
            timestamp = this@toMessageResponse.timestamp
            isRead = this@toMessageResponse.isRead
        }
    ).build()

fun Message.toMessageRpc(): MessageRpc =
    Message {
        id = this@toMessageRpc.id.value
        content = body
        senderId = sender
        receiverId = receiver
        timestamp = this@toMessageRpc.timestamp
        isRead = this@toMessageRpc.isRead
    }

inline fun Message(builder: MessageRpc.Builder.() -> Unit): MessageRpc =
    MessageRpc.newBuilder().apply(builder).build()