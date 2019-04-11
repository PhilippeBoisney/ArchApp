package io.philippeboisney.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.philippeboisney.common_test.rules.CoroutinesMainDispatcherRule
import io.philippeboisney.local.dao.UserDao
import io.philippeboisney.model.ApiResult
import io.philippeboisney.model.User
import io.philippeboisney.remote.UserDatasource
import io.philippeboisney.repository.utils.FakeData
import io.philippeboisney.repository.utils.Resource
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    // FOR DATA
    private lateinit var observer: Observer<Resource<List<User>>>
    private lateinit var observerUser: Observer<Resource<User>>
    private lateinit var userRepository: UserRepository
    private val userService = mockk<UserDatasource>()
    private val userDao = mockk<UserDao>(relaxed = true)

    @Before
    fun setUp() {
        observer = mockk(relaxed = true)
        observerUser = mockk(relaxed = true)
        userRepository = UserRepositoryImpl(userService, userDao)
    }

    @Test
    fun `Get top users from server when no internet is available`() {
        val exception = Exception("Internet")
        every { userService.fetchTopUsersAsync() } throws exception
        coEvery { userDao.getTopUsers() } returns listOf()

        runBlocking {
            userRepository.getTopUsersWithCache().observeForever(observer)
        }

        verifyOrder {
            observer.onChanged(Resource.loading(null)) // Init loading with no value
            observer.onChanged(Resource.loading(listOf())) // Then trying to load from db (fast temp loading) before load from remote source
            observer.onChanged(Resource.error(exception, listOf())) // Retrofit 403 error
        }
        confirmVerified(observer)
    }


    @Test
    fun `Get top users from network`() {
        val fakeUsers = FakeData.createFakeUsers(5)
        every { userService.fetchTopUsersAsync() } returns GlobalScope.async { ApiResult(fakeUsers.size, fakeUsers) }
        coEvery { userDao.getTopUsers() } returns listOf() andThen { fakeUsers }

        runBlocking {
            userRepository.getTopUsersWithCache().observeForever(observer)
        }

        verifyOrder {
            observer.onChanged(Resource.loading(null)) // Loading from remote source
            observer.onChanged(Resource.loading(listOf())) // Then trying to load from db (fast temp loading) before load from remote source
            observer.onChanged(Resource.success(fakeUsers)) // Success
        }

        coVerify(exactly = 1) {
            userDao.save(fakeUsers)
        }

        confirmVerified(observer)
    }

    @Test
    fun `Get top users from db`() {
        val fakeUsers = FakeData.createFakeUsers(5)
        every { userService.fetchTopUsersAsync() } returns GlobalScope.async { ApiResult(fakeUsers.size, fakeUsers) }
        coEvery { userDao.getTopUsers() } returns fakeUsers

        runBlocking {
            userRepository.getTopUsersWithCache().observeForever(observer)
        }

        verifyOrder {
            observer.onChanged(Resource.loading(null)) // Loading from remote source
            observer.onChanged(Resource.success(fakeUsers)) // Success
        }

        confirmVerified(observer)
    }

    @Test
    fun `Get user's detail from network`() {
        val fakeUser = FakeData.createFakeUser("fake")
        every { userService.fetchUserDetailsAsync("fake_login") } returns GlobalScope.async { fakeUser }
        coEvery { userDao.getUser("fake_login") } returns fakeUser

        runBlocking {
            userRepository.getUserDetailWithCache(login = "fake_login").observeForever(observerUser)
        }

        verify {
            observerUser.onChanged(Resource.loading(null)) // Loading from remote source
            observerUser.onChanged(Resource.loading(fakeUser)) // Then trying to load from db (fast temp loading) before load from remote source
            observerUser.onChanged(Resource.success(fakeUser)) // Success
        }

        coVerify(exactly = 1) {
            userDao.save(fakeUser)
        }

        confirmVerified(observerUser)
    }

    @Test
    fun `Get user's detail from db`() {
        val fakeUser = FakeData.createFakeUser("fake")

        every { userService.fetchUserDetailsAsync("fake_login") } returns GlobalScope.async { fakeUser }
        coEvery { userDao.getUser("fake_login") } returns fakeUser.apply { lastRefreshed = Date() }

        runBlocking {
            userRepository.getUserDetailWithCache(login = "fake_login").observeForever(observerUser)
        }

        verify {
            observerUser.onChanged(Resource.loading(null)) // Loading from remote source
            observerUser.onChanged(Resource.success(fakeUser)) // Success
        }

        confirmVerified(observerUser)
    }

}