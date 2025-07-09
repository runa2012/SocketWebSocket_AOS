package kr.co.socketwebsocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HeartbeatManager (
    private val sendPing: suspend () -> Unit,
    private val onTimeout: () -> Unit,
    private val interval: Long = 10_000,
    private val timeout: Long = 15_000
) {
    private var lastPongTime = System.currentTimeMillis()
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        job = scope.launch {
            while (isActive) {
                sendPing()
                delay(interval)
                if (System.currentTimeMillis() - lastPongTime > timeout) {
                    onTimeout()
                    break
                }
            }
        }
    }

    fun receivedPong() {
        lastPongTime = System.currentTimeMillis()
    }

    fun stop() {
        job?.cancel()
    }
}