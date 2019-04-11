package io.philippeboisney.detail.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.philippeboisney.model.User
import io.philippeboisney.repository.utils.Resource
import io.philippeboisney.repository.UserRepository

/**
 * Use case that gets a [Resource] [User] from [UserRepository]
 * and makes some specific logic actions on it.
 *
 * In this Use Case, I'm just doing nothing... ¯\_(ツ)_/¯
 */
class GetUserDetailUseCase(private val repository: UserRepository) {

    suspend operator fun invoke(forceRefresh: Boolean = false, login: String): LiveData<Resource<User>> {
        return Transformations.map(repository.getUserDetailWithCache(forceRefresh, login)) {
            it // Place here your specific logic actions (if any)
        }
    }
}