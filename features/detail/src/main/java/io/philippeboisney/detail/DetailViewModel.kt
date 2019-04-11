package io.philippeboisney.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.philippeboisney.common.base.BaseViewModel
import io.philippeboisney.common.utils.Event
import io.philippeboisney.detail.domain.GetUserDetailUseCase
import io.philippeboisney.model.User
import io.philippeboisney.repository.AppDispatchers
import io.philippeboisney.repository.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [BaseViewModel] that provide the data and handle logic to communicate with the model
 * for [DetailFragment].
 */
class DetailViewModel(private val getUserDetailUseCase: GetUserDetailUseCase,
                      private val dispatchers: AppDispatchers): BaseViewModel() {

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

    fun userClicksOnAvatarImage(user: User)
            = navigate(DetailFragmentDirections.actionDetailFragmentToImageDetailFragment(user.avatarUrl))

    // ---

    private fun getUserDetail(forceRefresh: Boolean) = viewModelScope.launch(dispatchers.main) {
        dataSources.removeSource(liveDataUser) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(dispatchers.io) { liveDataUser = getUserDetailUseCase(forceRefresh = forceRefresh, login = argsLogin) }
        dataSources.addSource(liveDataUser) {
            dataSources.value = it.data
            isLoading.value = it.status
            if (it.status == Resource.Status.ERROR) _snackbarError.value = Event(R.string.an_error_happened)
        }
    }
}