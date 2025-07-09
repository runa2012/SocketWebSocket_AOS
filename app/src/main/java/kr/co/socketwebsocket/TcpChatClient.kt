package kr.co.socketwebsocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

sealed class ConnectionState {
    object Connected : ConnectionState()
    object Disconnected : ConnectionState()
    object Reconnecting : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

class TcpChatClient (
    private val host: String,
    private val port: Int
) {
    private var socket: Socket? = null
    private var input: DataInputStream? = null
    private var output: DataOutputStream? = null

    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    suspend fun connectWithRetry(maxRetries: Int = 3) {
        var attempt = 0
        var delayTime = 1000L
        while (attempt < maxRetries) {
            try {
                connect()
                return
            } catch (e: IOException) {
                _connectionState.value = ConnectionState.Reconnecting
                delay(delayTime)
                delayTime *= 2
                attempt++
            }
        }
        _connectionState.value = ConnectionState.Error("Connection failed after $maxRetries attempts")
    }

    suspend fun connect() = withContext(Dispatchers.IO) {
        _connectionState.value = ConnectionState.Reconnecting
        socket = Socket(host, port)
        input = DataInputStream(socket!!.getInputStream())
        output = DataOutputStream(socket!!.getOutputStream())
        _connectionState.value = ConnectionState.Connected
        readLoop()
    }

    private suspend fun readLoop() = withContext(Dispatchers.IO) {
        try {
            while (true) {
                val length = input!!.readInt()
                val data = ByteArray(length)
                input!!.readFully(data)
                val message = String(data)
                _messages.emit(message)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    suspend fun sendMessage(message: String) = withContext(Dispatchers.IO) {
        try {
            val data = message.toByteArray()
            output?.let { it ->
                it.writeInt(data.size)
                it.write(data)
                it.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _connectionState.value = ConnectionState.Disconnected
        }
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        socket?.close()
        _connectionState.value = ConnectionState.Disconnected
    }
}
