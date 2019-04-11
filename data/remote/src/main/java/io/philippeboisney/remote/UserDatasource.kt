package io.philippeboisney.remote

/**
 * Implementation of [UserService] interface
 */
class UserDatasource(private val userService: UserService) {

    fun fetchTopUsersAsync() =
            userService.fetchTopUsersAsync()

    fun fetchUserDetailsAsync(login: String) =
            userService.fetchUserDetailsAsync(login)
}