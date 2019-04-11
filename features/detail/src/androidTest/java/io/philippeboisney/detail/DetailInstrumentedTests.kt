package io.philippeboisney.detail

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import io.philippeboisney.common_test.datasets.UserDataset.FAKE_USERS
import io.philippeboisney.detail.di.featureDetailModule
import io.philippeboisney.model.User
import io.philippeboisney.repository.AppDispatchers
import io.philippeboisney.repository.UserRepository
import io.philippeboisney.repository.utils.Resource
import kotlinx.coroutines.Dispatchers
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
@LargeTest
class DetailInstrumentedTests: KoinTest {

    private val userRepository = mockk<UserRepository>()

    @Before
    fun setUp(){
        StandAloneContext.loadKoinModules(featureDetailModule, module {
            factory { AppDispatchers(Dispatchers.Main, Dispatchers.Main) }
            factory { userRepository }
        })
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun testIfUserIsCorrectlyDisplayed() {
        val user = FAKE_USERS.first()
        coEvery { userRepository.getUserDetailWithCache(false, "fake") } returns MutableLiveData<Resource<User>>().apply { postValue(Resource.success(user)) }
        launchFragment()

        onView(ViewMatchers.withId(R.id.fragment_detail_blog)).check(matches(withText(containsString(user.blog))))
        onView(ViewMatchers.withId(R.id.fragment_detail_company)).check(matches(withText(containsString(user.company))))
        onView(ViewMatchers.withId(R.id.fragment_detail_name)).check(matches(withText(containsString(user.name))))
    }

    @Test
    fun testRefreshWhenError() {
        coEvery { userRepository.getUserDetailWithCache(any(), "fake") } returns MutableLiveData<Resource<User>>().apply { postValue(Resource.error(Exception("no_internet"), FAKE_USERS.first())) }
        launchFragment()

        onView(ViewMatchers.withId(R.id.fragment_detail_root_view)).perform(ViewActions.swipeDown())

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.an_error_happened)))
    }

    @Test
    fun testNavigationToDetailImageScreen() {
        coEvery { userRepository.getUserDetailWithCache(false, "fake") } returns MutableLiveData<Resource<User>>().apply { postValue(Resource.success(FAKE_USERS.first())) }
        val mockNavController = launchFragment()

        Espresso.onView(ViewMatchers.withId(R.id.fragment_detail_avatar)).perform(click())

        verify {
            mockNavController.navigate(DetailFragmentDirections.actionDetailFragmentToImageDetailFragment(FAKE_USERS.first().avatarUrl), any<FragmentNavigator.Extras>())
        }
    }

    // ---

    private fun launchFragment(): NavController {
        val mockNavController = mockk<NavController>(relaxed = true)
        val detailScenario = launchFragmentInContainer<DetailFragment>(fragmentArgs = Bundle().apply { putString("login", "fake") }, themeResId = R.style.AppTheme)
        detailScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        return mockNavController
    }
}