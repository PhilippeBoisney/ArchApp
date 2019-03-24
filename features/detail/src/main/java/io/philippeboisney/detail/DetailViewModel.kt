package io.philippeboisney.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.philippeboisney.common.base.BaseViewModel
import io.philippeboisney.common.utils.Event
import io.philippeboisney.model.User
import io.philippeboisney.repository.Resource
import io.philippeboisney.repository.UserRepository

class DetailViewModel(private val repository: UserRepository): BaseViewModel() {

    // PRIVATE DATA
    private val dataSources = MediatorLiveData<User>()
    private var liveDataUser: LiveData<Resource<User>> = MutableLiveData()
    private val isLoading = MutableLiveData<Resource.Status>()
    private lateinit var argsLogin: String

    // PUBLIC LIVEDATA's ---
    fun getUser(): LiveData<User>
            = dataSources

    fun getIsLoading(): LiveData<Resource.Status>
            = isLoading

    // PUBLIC ACTIONS ---
    fun loadDataWhenActivityStarts(login: String) {
        argsLogin = login
        getUserDetail(false)
    }

    fun reloadDataWhenUserRefreshes()
            = getUserDetail(true)


    // ---

    private fun getUserDetail(forceRefresh: Boolean) {
        dataSources.removeSource(liveDataUser) // We make sure there is only one source of livedata (allowing us properly refresh)
        liveDataUser = repository.getUserDetail(forceRefresh = forceRefresh, login = argsLogin, scope = viewModelScope)
        dataSources.addSource(liveDataUser) {
            dataSources.value = it.data
            isLoading.value = it.status
            if (it.status == Resource.Status.ERROR) snackbarError.value = Event(R.string.an_error_happened)
        }
    }
}