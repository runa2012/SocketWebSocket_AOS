package kr.co.socketwebsocket

import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen() {
    val client = remember { TcpChatClient("127.0.0.1", 9999) }
    val viewModel: ChatViewModel = ChatViewModel(client)
    val message by viewModel.messages.collectAsState()
    val state by viewModel.connectionState.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("🔌 상태: ${state.javaClass.simpleName}")
        Spacer(modifier = Modifier.height(8.dp))

        Text("📨 수신: $message")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("메시지 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .clickable{
                    viewModel.send("전송 메시지")
                },
           text =  "전송")
    }
}
