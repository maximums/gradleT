package cdodi.com.data

import cdodi.com.data.DatabaseFactory.dbQuery
import cdodi.com.data.model.User
import cdodi.com.data.model.Users
import com.cdodi.data.*

class UserService : DataServiceGrpcKt.DataServiceCoroutineImplBase() {
    override suspend fun createUser(request: UserRequest): UserResponse = dbQuery {
        User.new {
            name = request.name
            email = request.email
            password = request.password
            dateOfBirth = request.dateOfBirth
            accountCreationTime = 789
            hobbiesIds = request.hobbiesIds
            avatarID = request.avatarId
        }.toUserResponse()
    }

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