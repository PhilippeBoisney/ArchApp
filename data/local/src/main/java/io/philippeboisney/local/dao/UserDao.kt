package io.philippeboisney.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.philippeboisney.model.User
import java.util.*

@Dao
abstract class UserDao: BaseDao<User>() {

    @Query("SELECT * FROM User ORDER BY login ASC LIMIT 30")
    abstract fun getTopUsers(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE login = :login LIMIT 1")
    abstract fun getUser(login: String): LiveData<User>

    // ---

    /**
     * Each time we save an user, we update its 'lastRefreshed' field
     * This allows us to know when we have to refresh its data
     */

    suspend fun save(user: User) {
        insert(user.apply { lastRefreshed = Date() })
    }

    suspend fun save(users: List<User>) {
        insert(users.apply { forEach { it.lastRefreshed = Date() } })
    }
}