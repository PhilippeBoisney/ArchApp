package io.philippeboisney.local

import io.philippeboisney.common_test.extensions.blockingObserve
import io.philippeboisney.local.base.BaseTest
import io.philippeboisney.model.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*

class UserDaoTest: BaseTest() {

    override fun setUp(){
        super.setUp()
        fillDatabase()
    }

    @Test
    fun getTopUsersFromDb(){
        val users = database.userDao().getTopUsers().blockingObserve()!!
        assertEquals(3, users.size)
        compareTwoUsers(FAKE_USERS.first(), users.first())
    }

    @Test
    fun getUser(){
        val user = database.userDao().getUser(FAKE_USERS.first().login).blockingObserve()!!
        compareTwoUsers(FAKE_USERS.first(), user)
    }

    @Test
    fun saveUser_DateMustChange() = runBlocking {
            database.userDao().save(FAKE_USERS.first())
            val user = database.userDao().getUser(FAKE_USERS.first().login).blockingObserve()!!
            assertNotEquals(DATE_REFRESH, user.lastRefreshed)
    }

    @Test
    fun saveUsers_DateMustChange() = runBlocking {
        database.userDao().save(FAKE_USERS)
        val users = database.userDao().getTopUsers().blockingObserve()!!
        assertNotEquals(DATE_REFRESH, users.first().lastRefreshed)
    }

    // ---

    private fun compareTwoUsers(user: User, userToTest: User){
        assertEquals(user.id, userToTest.id)
        assertEquals(user.name, userToTest.name)
        assertEquals(user.login, userToTest.login)
        assertEquals(user.avatarUrl, userToTest.avatarUrl)
        assertEquals(user.company, userToTest.company)
        assertEquals(user.blog, userToTest.blog)
        assertEquals(user.lastRefreshed, userToTest.lastRefreshed)
    }

    private fun fillDatabase() {
        runBlocking {
            database.userDao().save(FAKE_USERS)
        }
    }

    companion object {
        val DATE_REFRESH: Date = GregorianCalendar(2018, 5, 12).time
        val FAKE_USERS = listOf(
            User(id="Id_1", login = "Login_1", avatarUrl = "AvatarUrl_1", blog = "Blog1", company = "Company1", lastRefreshed = DATE_REFRESH, name = "Name1"),
            User(id="Id_2", login = "Login_2", avatarUrl = "AvatarUrl_2", blog = "Blog2", company = "Company2", lastRefreshed = DATE_REFRESH, name = "Name2"),
            User(id="Id_3", login = "Login_3", avatarUrl = "AvatarUrl_3", blog = "Blog3", company = "Company3", lastRefreshed = DATE_REFRESH, name = "Name3")
        )
    }
}