package kr.co.socketwebsocket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val client: TcpChatClient
) : ViewModel() {

    val messages = client.messages
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val connectionState = client.connectionState
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectionState.Disconnected)

    init {
        viewModelScope.launch {
            client.connectWithRetry()
        }
    }

    fun send(text: String) {
        viewModelScope.launch {
            client.sendMessage(text)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            client.close()
        }
    }
}