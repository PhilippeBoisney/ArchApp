package io.philippeboisney.repository

import androidx.lifecycle.LiveData
import io.philippeboisney.local.dao.UserDao
import io.philippeboisney.model.ApiResult
import io.philippeboisney.model.User
import io.philippeboisney.remote.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

class UserRepository(private val service: UserService,
                     private val dao: UserDao) {

    /**
     * TODO:
     */
    fun getTopUsers(forceRefresh: Boolean = false, scope: CoroutineScope): LiveData<Resource<List<User>>> {
        return object : NetworkBoundResource<List<User>, ApiResult<User>>(scope) {

            override fun processResponse(response: ApiResult<User>): List<User>
                    = response.items

            override suspend fun saveCallResults(items: List<User>)
                    = dao.save(items)

            override fun shouldFetch(data: List<User>?): Boolean
                    = data == null || data.isEmpty() || forceRefresh

            override fun loadFromDb(): LiveData<List<User>>
                    = dao.getTopUsers()

            override fun createCallAsync(): Deferred<ApiResult<User>>
                    = service.fetchTopUsersAsync()

        }.asLiveData()
    }

    /**
     * TODO:
     */
    fun getUserDetail(forceRefresh: Boolean = false, login: String, scope: CoroutineScope): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(scope) {

            override fun processResponse(response: User): User
                    = response

            override suspend fun saveCallResults(item: User)
                    = dao.save(item)

            override fun shouldFetch(data: User?): Boolean
                    = data == null
                    || data.haveToRefreshFromNetwork()
                    || data.name.isNullOrEmpty()
                    || forceRefresh

            override fun loadFromDb(): LiveData<User>
                    = dao.getUser(login)

            override fun createCallAsync(): Deferred<User>
                    = service.fetchUserDetailsAsync(login)

        }.asLiveData()
    }
}