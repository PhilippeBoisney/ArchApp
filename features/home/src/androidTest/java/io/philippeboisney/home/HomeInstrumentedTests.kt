package io.philippeboisney.home

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import io.philippeboisney.common_test.datasets.UserDataset.FAKE_USERS
import io.philippeboisney.common_test.espresso.RecyclerViewItemCountAssertion.Companion.withItemCount
import io.philippeboisney.home.di.featureHomeModule
import io.philippeboisney.model.User
import io.philippeboisney.repository.AppDispatchers
import io.philippeboisney.repository.UserRepository
import io.philippeboisney.repository.utils.Resource
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeInstrumentedTests: KoinTest {

    private val userRepository = mockk<UserRepository>()

    @Before
    fun setUp(){
        loadKoinModules(featureHomeModule, module {
            factory { AppDispatchers(Dispatchers.Main, Dispatchers.Main) }
            factory { userRepository }
        })
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testRecyclerViewContainsItems() {
        coEvery { userRepository.getTopUsersWithCache(false) } returns MutableLiveData<Resource<List<User>>>().apply { postValue(Resource.success(FAKE_USERS)) }
        launchFragment()

        onView(withId(R.id.fragment_home_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withId(R.id.fragment_home_rv)).check(withItemCount(3))
    }

    @Test
    fun testScreenBehaviorWhenError() {
        coEvery { userRepository.getTopUsersWithCache(false) } returns MutableLiveData<Resource<List<User>>>().apply { postValue(Resource.error(Exception("wtf"), listOf())) }
        launchFragment()

        onView(withId(R.id.fragment_home_text_view_empty_list)).check(matches(isDisplayed()))
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.an_error_happened)))
    }

    @Test
    fun testRefreshWhenError() {
        coEvery { userRepository.getTopUsersWithCache(any()) } returns MutableLiveData<Resource<List<User>>>().apply { postValue(Resource.error(Exception("no_internet"), FAKE_USERS)) }
        launchFragment()

        onView(ViewMatchers.withId(R.id.fragment_home_swipe_to_refresh)).perform(swipeDown())

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.an_error_happened)))
        onView(withId(R.id.fragment_home_rv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withId(R.id.fragment_home_rv)).check(withItemCount(3))
    }

    @Test
    fun testNavigationToDetailScreen() {
        coEvery { userRepository.getTopUsersWithCache(false) } returns MutableLiveData<Resource<List<User>>>().apply { postValue(Resource.success(FAKE_USERS)) }
        val mockNavController = launchFragment()

        onView(ViewMatchers.withId(R.id.fragment_home_rv)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        verify {
            mockNavController.navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment("Login_1"), any<FragmentNavigator.Extras>())
        }
    }

    // ---

    private fun launchFragment(): NavController {
        val mockNavController = mockk<NavController>(relaxed = true)
        val homeScenario = launchFragmentInContainer<HomeFragment>(themeResId = R.style.AppTheme)
        homeScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        return mockNavController
    }
}