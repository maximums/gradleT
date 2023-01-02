package cdodi.com.gateway

import arrow.core.Either
import cdodi.com.gateway.dto.*
import com.cdodi.data.*

inline fun Email(builder: UserEmail.Builder.() -> Unit): UserEmail =
    UserEmail.newBuilder().apply(builder).build()

inline fun ChatMessages(builder: ChatMessageRequest.Builder.() -> Unit): ChatMessageRequest =
    ChatMessageRequest.newBuilder().apply(builder).build()

fun UserResponse.toRestUserResponse(): Either<String, RestUserResponse> =
    if (hasUser()) Either.Right(
        RestUserResponse(
            user.id,
            user.name,
            user.email,
            user.password,
            user.avatarId,
            user.dateOfBirth,
            user.accountCreationTime,
            user.hobbiesIds,
        )
    )
    else Either.Left("Missing user")

fun User.toRestUserResponse(): RestUserResponse =
    RestUserResponse(
        id,
        name,
        email,
        password,
        avatarId,
        dateOfBirth,
        accountCreationTime,
        hobbiesIds,
    )

fun RestUserRequest.toUserRequest(): UserRequest =
    UserRequest.newBuilder()
        .setEmail(email)
        .setName(name)
        .setPassword(password)
        .setAvatarId(avatarId)
        .setDateOfBirth(dateOfBirth)
        .setHobbiesIds(hobbiesIds)
        .build()

inline fun MessageGetReq(builder: MessageGetReq.Builder.() -> Unit): MessageGetReq =
    MessageGetReq.newBuilder().apply(builder).build()

inline fun MessageEditRequest(builder: MessageEditRequest.Builder.() -> Unit): MessageEditRequest =
    MessageEditRequest.newBuilder().apply(builder).build()

fun MessageResponse.toRestMessageResponse(): Either<String, RestMessageResponse> =
    if (hasMessage()) Either.Right(
        RestMessageResponse(
            id = message.id,
            content = message.content,
            receiverId = message.receiverId,
            timestamp = message.timestamp,
            isRead = message.isRead
        )
    )
    else Either.Left("Missing Message")

fun Message.toRestMessageResponse(): RestMessageResponse =
    RestMessageResponse(
        id,
        content,
        receiverId,
        timestamp,
        isRead
    )

fun RestMessageRequest.toMessageRequest(senderId: Long): MessageRequest =
    MessageRequest.newBuilder()
        .setContent(content)
        .setSenderId(senderId)
        .setReceiverId(receiverId)
        .setIsRead(isRead)
        .build()

/////////

fun Message.toRestMessage(): RestMessage =
    RestMessage(
        id,
        content,
        receiverId,
        senderId,
        timestamp,
        isRead
    )

fun RestMessage.toMessageRequest(): MessageRequest =
    MessageRequest.newBuilder()
        .setContent(content)
        .setReceiverId(chatId)
        .setSenderId(senderId)
        .setIsRead(isRead)
        .build()