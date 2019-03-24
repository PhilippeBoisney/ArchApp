package io.philippeboisney.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.mockk.*
import io.philippeboisney.local.dao.UserDao
import io.philippeboisney.model.ApiResult
import io.philippeboisney.model.User
import io.philippeboisney.remote.UserService
import io.philippeboisney.repository.utils.FakeData
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class UserRepositoryTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    // FOR DATA
    private lateinit var observer: Observer<Resource<List<User>>>
    private lateinit var observerUser: Observer<Resource<User>>
    private lateinit var userRepository: UserRepository
    private val userService = mockk<UserService>()
    private val userDao = mockk<UserDao>(relaxed = true)

    @Before
    fun setUp() {
        observer = mockk(relaxed = true)
        observerUser = mockk(relaxed = true)
        userRepository = UserRepository(userService, userDao)
    }

    @Test
    fun `Get top users from server when no internet is available`() {
        val exception = Exception("Internet")
        every { userService.fetchTopUsersAsync() } throws exception
        every { userDao.getTopUsers() } returns MutableLiveData<List<User>> ().apply { value = listOf() }

        runBlocking {
            userRepository.getTopUsers(scope = this).observeForever(observer)
        }

        verifyOrder {
            observer.onChanged(Resource.loading(null)) // Loading from remote source
            observer.onChanged(Resource.loading(listOf())) // Then trying to load from db (fast temp loading)
            observer.onChanged(Resource.error(exception, listOf())) // Retrofit 403 error
        }
        confirmVerified(observer)
    }

    @Test
    fun `Get top users from network`() {
        val fakeUsers = FakeData.createFakeUsers(5)
        every { userService.fetchTopUsersAsync() } returns GlobalScope.async { ApiResult(fakeUsers.size, fakeUsers) }
        every { userDao.getTopUsers() } returns MutableLiveData<List<User>> ().apply { value = listOf() } andThen { MutableLiveData<List<User>> ().apply { value = fakeUsers } }

        runBlocking {
            userRepository.getTopUsers(scope = this).observeForever(observer)
        }

        verifyOrder {
            observer.onChanged(Resource.loading(null)) // Loading from remote source
            observer.onChanged(Resource.loading(listOf())) // Then trying to load from db (fast temp loading)
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
        every { userDao.getTopUsers() } returns MutableLiveData<List<User>> ().apply { value = fakeUsers }

        runBlocking {
            userRepository.getTopUsers(scope = this).observeForever(observer)
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
        every { userDao.getUser("fake_login") } returns MutableLiveData<User>().apply { value = fakeUser }

        runBlocking {
            userRepository.getUserDetail(login = "fake_login", scope = this).observeForever(observerUser)
        }

        verify {
            observerUser.onChanged(Resource.loading(null)) // Loading from remote source
            observerUser.onChanged(Resource.loading(fakeUser)) // Then trying to load from db (fast temp loading)
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
        every { userDao.getUser("fake_login") } returns MutableLiveData<User>().apply { value = fakeUser.apply { lastRefreshed = Date() } }

        runBlocking {
            userRepository.getUserDetail(login = "fake_login", scope = this).observeForever(observerUser)
        }

        verify {
            observerUser.onChanged(Resource.loading(null)) // Loading from remote source
            observerUser.onChanged(Resource.success(fakeUser)) // Success
        }

        confirmVerified(observerUser)
    }
}