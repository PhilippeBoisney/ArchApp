package io.philippeboisney.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import io.philippeboisney.common.navigation.NavigationCommand
import io.philippeboisney.common.utils.Event

abstract class BaseViewModel: ViewModel() {

    // FOR ERROR HANDLER
    protected val snackbarError = MutableLiveData<Event<Int>>()

    // FOR NAVIGATION
    private val navigationCommands = MutableLiveData<Event<NavigationCommand>>()

    // PUBLIC LIVEDATA's ---
    fun getSnackBarError(): LiveData<Event<Int>>
            = snackbarError

    fun getNavigationCommands(): LiveData<Event<NavigationCommand>>
            = navigationCommands

    /**
     * TODO:
     */
     fun navigate(directions: NavDirections) {
        navigationCommands.value = Event(NavigationCommand.To(directions))
    }
}