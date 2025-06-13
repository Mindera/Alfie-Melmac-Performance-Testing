import io.mockk.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utils.Logger

class LoggerTest {

    @Test
    fun `info logs message with INFO level and timestamp`() {
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
        Logger.info("Hello info")
        System.out.flush()
        val output = outContent.toString()
        assertTrue(output.contains("INFO: Hello info"))
        assertTrue(output.contains("["))
        System.setOut(System.out)
    }

    @Test
    fun `error logs message with ERROR level and timestamp`() {
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
        Logger.error("Hello error")
        System.out.flush()
        val output = outContent.toString()
        assertTrue(output.contains("ERROR: Hello error"))
        assertTrue(output.contains("["))
        System.setOut(System.out)
    }

    @Test
    fun `debug logs message with DEBUG level and timestamp`() {
        val outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))
        Logger.debug("Hello debug")
        System.out.flush()
        val output = outContent.toString()
        assertTrue(output.contains("DEBUG: Hello debug"))
        assertTrue(output.contains("["))
        System.setOut(System.out)
    }
}