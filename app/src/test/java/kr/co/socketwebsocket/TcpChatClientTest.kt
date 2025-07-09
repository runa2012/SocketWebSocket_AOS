package kr.co.socketwebsocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TcpChatClientTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        LocalTestServer.start(port = 9999)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testMessageEcho() = runTest {
        val client = TcpChatClient("127.0.0.1", 9999)
        client.connectWithRetry()

        val messages = mutableListOf<String>()
        val job = launch {
            client.messages.collect {
                messages.add(it)
            }
        }

        client.sendMessage("Hello Test")
        advanceTimeBy(1000)

        assert(messages.any { it.contains("Hello Test") })

        job.cancel()
        client.close()
    }

}