package utils

/**
 * Utility object for logging messages. Provides methods to log informational, error, and debug
 * messages.
 */
object Logger {

    /**
     * Logs an informational message.
     *
     * @param message The message to log.
     */
    fun info(message: String) {
        println("INFO: $message")
        System.out.flush()
    }

    /**
     * Logs an error message.
     *
     * @param message The message to log.
     */
    fun error(message: String) {
        println("ERROR: $message")
        System.out.flush()
    }

    /**
     * Logs a debug message.
     *
     * @param message The message to log.
     */
    fun debug(message: String) {
        println("DEBUG: $message")
        System.out.flush()
    }
}
