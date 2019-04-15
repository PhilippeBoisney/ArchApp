package io.philippeboisney.remote.base

import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import io.philippeboisney.remote.UserService
import io.philippeboisney.remote.di.createRemoteModule
import org.junit.After
import org.junit.Before
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import java.io.File

abstract class BaseTest: KoinTest {

    protected val userService: UserService by inject()
    protected lateinit var mockServer: MockWebServer

    @Before
    open fun setUp(){
        this.configureMockServer()
        this.configureDi()
    }

    @After
    open fun tearDown(){
        this.stopMockServer()
        StandAloneContext.stopKoin()
    }

    // CONFIGURATION
    private fun configureDi(){
        StandAloneContext.startKoin(listOf(createRemoteModule(mockServer.url("/").toString())))
    }

    private fun configureMockServer(){
        mockServer = MockWebServer()
        mockServer.start()
    }

    private fun stopMockServer() {
        mockServer.shutdown()
    }

    fun mockHttpResponse(mockServer: MockWebServer, fileName: String, responseCode: Int) = mockServer.enqueue(
        MockResponse()
            .setResponseCode(responseCode)
            .setBody(getJson(fileName)))

    private fun getJson(path : String) : String {
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }
}