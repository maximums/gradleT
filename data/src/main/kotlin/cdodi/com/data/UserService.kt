package cdodi.com.data

import com.cdodi.data.*

class UserService :DataServiceGrpcKt.DataServiceCoroutineImplBase() {
    override suspend fun createUser(request: UserRequest): UserResponse {
        println("createUser::::")
        return super.createUser(request)
    }

    override suspend fun getUser(request: UserEmail): UserResponse {
        println("getUser::::")
        return super.getUser(request)
    }

    override suspend fun getAllUsers(request: AllUsersRequest): AllUsersResponse {
        println("getAllUsers::::")
        return super.getAllUsers(request)
    }

    override suspend fun deleteUser(request: UserEmail): UserDeleteResult {
        println("deleteUser::::")
        return super.deleteUser(request)
    }
}