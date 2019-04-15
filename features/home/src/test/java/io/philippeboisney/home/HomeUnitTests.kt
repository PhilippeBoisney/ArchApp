package io.philippeboisney.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.filters.SmallTest
import io.mockk.*
import io.philippeboisney.common.utils.Event
import io.philippeboisney.common_test.datasets.UserDataset.FAKE_USERS
import io.philippeboisney.common_test.extensions.blockingObserve
import io.philippeboisney.home.domain.GetTopUsersUseCase
import io.philippeboisney.model.User
import io.philippeboisney.navigation.NavigationCommand
import io.philippeboisney.repository.AppDispatchers
import io.philippeboisney.repository.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class HomeUnitTests {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var getTopUsersUseCase: GetTopUsersUseCase
    private lateinit var homeViewModel: HomeViewModel
    private val appDispatchers = AppDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined)

    @Before
    fun setUp() {
        getTopUsersUseCase = mockk()
    }

    @Test
    fun `Users requested when ViewModel is created`() {
        val observer = mockk<Observer<Resource<List<User>>>>(relaxed = true)
        val result = Resource.success(FAKE_USERS)
        coEvery { getTopUsersUseCase(false) } returns MutableLiveData<Resource<List<User>>>().apply { value = result }

        homeViewModel = HomeViewModel(getTopUsersUseCase, appDispatchers)
        homeViewModel.users.observeForever(observer)

        verify {
            observer.onChanged(result)
        }

        confirmVerified(observer)
    }

    @Test
    fun `Users requested but failed when ViewModel is created`() {
        val observer = mockk<Observer<Resource<List<User>>>>(relaxed = true)
        val observerSnackbar = mockk<Observer<Event<Int>>>(relaxed = true)
        val result = Resource.error(Exception("fail"), null)
        coEvery { getTopUsersUseCase(any()) } returns  MutableLiveData<Resource<List<User>>>().apply { value = result }

        homeViewModel = HomeViewModel(getTopUsersUseCase, appDispatchers)
        homeViewModel.users.observeForever(observer)
        homeViewModel.snackBarError.observeForever(observerSnackbar)

        verify {
            observer.onChanged(result)
            observerSnackbar.onChanged(homeViewModel.snackBarError.value)
        }

        confirmVerified(observer)
    }

    @Test
    fun `User clicks on item on RecyclerView`() {
        val event = Event(NavigationCommand.To(HomeFragmentDirections.actionHomeFragmentToDetailFragment(FAKE_USERS.first().login)))
        coEvery { getTopUsersUseCase(false) } returns MutableLiveData<Resource<List<User>>>().apply { value = Resource.success(FAKE_USERS) }

        homeViewModel = HomeViewModel(getTopUsersUseCase, appDispatchers)
        homeViewModel.userClicksOnItem(FAKE_USERS.first())

        Assert.assertEquals(event.peekContent(), homeViewModel.navigation.blockingObserve()!!.peekContent())
    }

    @Test
    fun `User refreshes list with swipe to refresh`() {
        val observer = mockk<Observer<Resource<List<User>>>>(relaxed = true)
        val result = Resource.success(FAKE_USERS)
        coEvery { getTopUsersUseCase(any()) } returns MutableLiveData<Resource<List<User>>>().apply { value = result }

        homeViewModel = HomeViewModel(getTopUsersUseCase, appDispatchers)
        homeViewModel.users.observeForever(observer)
        homeViewModel.userRefreshesItems()

        verify(exactly = 2) {
            observer.onChanged(result) // When VM is created
            observer.onChanged(result) // When user actually refreshes
        }

        confirmVerified(observer)
    }
}