package utils

object Logger {
    fun info(message: String) = println("INFO: $message")
    fun error(message: String) = println("ERROR: $message")
    fun debug(message: String) = println("DEBUG: $message")
}