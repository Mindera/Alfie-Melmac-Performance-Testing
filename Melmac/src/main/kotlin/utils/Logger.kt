package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun timestamp(): String = LocalDateTime.now().format(formatter)

    fun info(message: String) {
        println("[${timestamp()}] INFO: $message")
        System.out.flush()
    }

    fun error(message: String) {
        println("[${timestamp()}] ERROR: $message")
        System.out.flush()
    }

    fun debug(message: String) {
        println("[${timestamp()}] DEBUG: $message")
        System.out.flush()
    }
}