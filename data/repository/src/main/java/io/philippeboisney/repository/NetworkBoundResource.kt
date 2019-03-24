package io.philippeboisney.repository

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.*

abstract class NetworkBoundResource<ResultType, RequestType>(private val coroutineScope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<ResultType>>()
    private val supervisorJob = SupervisorJob()

    init {
        setValue(Resource.loading(null))
        val dbSource = this.loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            coroutineScope.launch(getErrorHandler() + supervisorJob) {
                if (shouldFetch(it)) {
                    fetchFromNetwork(dbSource)
                } else {
                    Log.d(NetworkBoundResource::class.java.name, "Return data from local database")
                    result.addSource(dbSource) { setValue(Resource.success(it)) }
                }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        Log.d(NetworkBoundResource::class.java.name, "Fetch data from network")
        result.addSource(dbSource) { setValue(Resource.loading(it)) } // Dispatch latest value quickly (UX purpose)
        coroutineScope.launch(getErrorHandler() + supervisorJob) {
            result.removeSource(dbSource)
            Log.e(NetworkBoundResource::class.java.name, "Data fetched from network")
            val apiResponse = createCallAsync().await()
            saveCallResults(processResponse(apiResponse))

            result.addSource(loadFromDb()) { setValue(Resource.success(it)) }
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) result.value = newValue
    }

    @WorkerThread
    protected abstract fun processResponse(response: RequestType): ResultType

    @WorkerThread
    protected abstract suspend fun saveCallResults(items: ResultType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCallAsync(): Deferred<RequestType>

    private fun getErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e("NetworkBoundResource", "An error happened: $e")
        result.addSource(loadFromDb()) { setValue(Resource.error(e, it)) }
    }
}