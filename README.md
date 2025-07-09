# Android TCP Chat Client (Coroutine 기반)

Android에서 Kotlin Coroutine을 활용해 구현한 TCP 소켓 기반 채팅 클라이언트입니다.  
Jetpack Compose 기반 UI와 상태 관리, Ping/Heartbeat, 자동 재연결, 단위 테스트까지 
학습중인 내용

---

## 기능 요약

- **Coroutine 기반 TCP 소켓 통신**
- **Length-prefixed 프로토콜 처리**
- **연결 상태 관리 (Connected / Reconnecting / Disconnected / Error)**
- **Ping / Heartbeat 체크 및 연결 유지**
- **Jetpack Compose UI**
- **JUnit 단위 테스트 + Mock TCP 서버**

---

## 프로젝트 구조
<br/>
📦src<br/>
┣ 📁main<br/>
┃ ┣ 📄TcpChatClient.kt # 소켓 통신 클라이언트<br/>
┃ ┣ 📄HeartbeatManager.kt # 핑/타임아웃 체크<br/>
┃ ┣ 📄ChatViewModel.kt # UI 상태 관리<br/>
┃ ┣ 📄ChatScreen.kt # Compose UI 화면<br/>
┃ ┗ 📄LocalTestServer.kt # 로컬 테스트용 에코 서버<br/>
┣ 📁test<br/>
┃ ┗ 📄TcpChatClientTest.kt # 단위 테스트<br/>

---

TcpChatClient에서 "127.0.0.1", 9999로 접속하여 메시지를 전송하고 응답을 수신합니다.

---

연결 상태 관리<br/>
sealed class ConnectionState {<br/>
    object Connected : ConnectionState()<br/>
    object Disconnected : ConnectionState()<br/>
    object Reconnecting : ConnectionState()<br/>
    data class Error(val message: String) : ConnectionState()<br/>
}<br/>

---

단위 테스트 (TcpChatClientTest.kt)<br/>
@Test<br/>
fun testMessageEcho() = runTest {<br/>
    val client = TcpChatClient("127.0.0.1", 9999)<br/>
    client.connectWithRetry()<br/>
    client.sendMessage("Hello Test")<br/>
    assertTrue(messages.any { it.contains("Hello Test") })<br/>
}<br/>

---

향후 작업 내용
- 메시지 암호화 (AES/SSL 등)-
- 이미지 전송 (Base64 or Multipart)
- Server push 또는 broadcast 수신
- Android Service로 백그라운드 처리
- 실제 서버와 연동한 멀티 유저 채팅
