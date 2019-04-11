package io.philippeboisney.home.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.philippeboisney.model.User
import io.philippeboisney.repository.utils.Resource
import io.philippeboisney.repository.UserRepository

/**
 * Use case that gets a [Resource][List][User] from [UserRepository]
 * and makes some specific logic actions on it.
 *
 * In this Use Case, I'm just doing nothing... ¯\_(ツ)_/¯
 */
class GetTopUsersUseCase(private val repository: UserRepository) {

    suspend operator fun invoke(forceRefresh: Boolean = false): LiveData<Resource<List<User>>> {
        return Transformations.map(repository.getTopUsersWithCache(forceRefresh)) {
            it // Place here your specific logic actions
        }
    }
}