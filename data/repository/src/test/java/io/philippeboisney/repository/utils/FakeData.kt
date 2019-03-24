package io.philippeboisney.repository.utils

import io.philippeboisney.model.User
import java.util.*

object FakeData {
    fun createFakeUsers(count: Int): List<User> {
        return (0 until count).map {
            createFakeUser(it.toString())
        }
    }

    fun createFakeUser(id: String): User {
        return User(id="Id$id", login = "Login_$id", avatarUrl = "AvatarUrl_$id", blog = "Blog$id", company = "Company$id", lastRefreshed = DATE_REFRESH, name = "Name$id")

    }
    private val DATE_REFRESH: Date = GregorianCalendar(2018, 5, 12).time
}