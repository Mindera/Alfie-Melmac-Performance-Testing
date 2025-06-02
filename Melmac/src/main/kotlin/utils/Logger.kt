package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility object for logging messages with timestamps.
 * Provides methods for info, error, and debug level logging.
 */
object Logger {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * Returns the current timestamp formatted as a string.
     * @return the formatted timestamp
     */
    private fun timestamp(): String = LocalDateTime.now().format(formatter)

    /**
     * Logs an informational message with a timestamp.
     * @param message the message to log
     */
    fun info(message: String) {
        println("[${timestamp()}] INFO: $message")
        System.out.flush()
    }

    /**
     * Logs an error message with a timestamp.
     * @param message the message to log
     */
    fun error(message: String) {
        println("[${timestamp()}] ERROR: $message")
        System.out.flush()
    }

    /**
     * Logs a debug message with a timestamp.
     * @param message the message to log
     */
    fun debug(message: String) {
        println("[${timestamp()}] DEBUG: $message")
        System.out.flush()
    }
}