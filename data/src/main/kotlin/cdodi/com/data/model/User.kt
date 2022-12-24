@file:Suppress("unused")

package cdodi.com.data.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    val name = varchar("name", 50).nullable()
    val email = varchar("email", 30)
    val password = varchar("password", 50)
    val dateOfBirth = long("dateOfBirth")
    val accountCreationTime = long("accountCreationTime")
    var avatarID = integer("avatarID").default(1)
    var hobbiesIds = varchar("hobbiesIds", 50)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var password by Users.password
    var dateOfBirth by Users.dateOfBirth
    var accountCreationTime by Users.accountCreationTime
    var avatarID by Users.avatarID
    var hobbiesIds by Users.hobbiesIds
}
