package io.philippeboisney.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.philippeboisney.common.base.BaseViewModel
import io.philippeboisney.common.utils.Event
import io.philippeboisney.model.User
import io.philippeboisney.repository.Resource
import io.philippeboisney.repository.UserRepository

class HomeViewModel(private val repository: UserRepository) : BaseViewModel() {

    // PRIVATE DATA ---
    private val dataSources = MediatorLiveData<Resource<List<User>>>()
    private var liveDataUsers: LiveData<Resource<List<User>>> = MutableLiveData()

    init {
        getUsers(false)
    }

    // PUBLIC LIVEDATA ---
    fun getUsers(): LiveData<Resource<List<User>>>
            = dataSources

    // PUBLIC ACTIONS ---
    fun userClicksOnItem(user: User)
            = navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(user.login))

    fun userRefreshesItems()
            = getUsers(true)

    // ---

    private fun getUsers(forceRefresh: Boolean) {
        dataSources.removeSource(liveDataUsers) // We make sure there is only one source of livedata (allowing us properly refresh)
        liveDataUsers = repository.getTopUsers(forceRefresh = forceRefresh, scope = viewModelScope)
        dataSources.addSource(liveDataUsers) {
            dataSources.value = it
            if (it.status == Resource.Status.ERROR) snackbarError.value = Event(R.string.an_error_happened)
        }
    }
}