package cdodi.com.data.services

import cdodi.com.data.DatabaseFactory.dbQuery
import cdodi.com.data.model.User
import cdodi.com.data.model.Users
import cdodi.com.data.model.toUserResponse
import cdodi.com.data.model.toUserRpc
import com.cdodi.data.*
import java.util.*

class UserService : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    override suspend fun createUser(request: UserRequest): UserResponse =
        if (isEmailRegistered(request.email))
            dbQuery {
                User.new {
                    name = request.name
                    email = request.email
                    password = request.password
                    dateOfBirth = request.dateOfBirth
                    accountCreationTime = Date().time
                    hobbiesIds = request.hobbiesIds
                    avatarID = request.avatarId
                }.toUserResponse()
            } else UserResponse.newBuilder().setIsNull(true).build()

    override suspend fun getUser(request: UserEmail): UserResponse = dbQuery {
        User.find { Users.email eq request.email }.firstOrNull().toUserResponse()
    }

    override suspend fun getAllUsers(request: AllUsersRequest): AllUsersResponse = dbQuery {
        val users = User.all().map(User::toUserRpc)
        AllUsersResponse.newBuilder().addAllUsers(users).build()
    }

    override suspend fun deleteUser(request: UserEmail): UserDeleteResult = dbQuery {
        val user = User.find { Users.email eq request.email }.firstOrNull()
        user?.delete()
        val isDeleted = user != null

        UserDeleteResult.newBuilder().setIsDeleted(isDeleted).build()
    }
}

suspend fun isEmailRegistered(email: String): Boolean = dbQuery {
    User.find { Users.email eq email }.count() > 0
}

suspend fun isUserRegistered(id: Long): Boolean = dbQuery {
    User.find { Users.id eq id }.count() > 0
}