package kr.co.socketwebsocket

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import kotlin.concurrent.thread

object LocalTestServer {
    fun start(port: Int = 9999) {
        thread {
            try {
                val serverSocket = ServerSocket(port)
                println("Server started on port $port")
                while (true) {
                    val clientSocket = serverSocket.accept()
                    thread {
                        val input = DataInputStream(clientSocket.getInputStream())
                        val output = DataOutputStream(clientSocket.getOutputStream())
                        try {
                            while (true) {
                                val length = input.readInt()
                                val data = ByteArray(length)
                                input.readFully(data)
                                val message = String(data)
                                println("Received: $message")
                                val response = "Echo: $message".toByteArray()
                                output.writeInt(response.size)
                                output.write(response)
                                output.flush()
                            }
                        } catch (e: IOException) {
                            println("Client disconnected")
                        } finally {
                            clientSocket.close()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}