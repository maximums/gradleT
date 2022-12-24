package cdodi.com.data

import cdodi.com.data.model.User
import com.cdodi.data.User as UserRpc
import com.cdodi.data.UserResponse

fun User?.toUserResponse(): UserResponse =
    if (this == null) UserResponse.newBuilder().setIsNull(true).build()
    else UserResponse.newBuilder().setUser(
        User {
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