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
    val dateOfBirth = long("dateOfBirth").nullable()
    val accountCreationDate = long("accountCreationDate")
    val avatarID = integer("avatarID").default(1)
    val hobbyIds = varchar("hobbyIds", 30)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var password by Users.password
    var dateOfBirth by Users.dateOfBirth
    var accountCreationDate by Users.accountCreationDate
    var avatarID by Users.avatarID
    var hobbyIds by Users.hobbyIds
}
