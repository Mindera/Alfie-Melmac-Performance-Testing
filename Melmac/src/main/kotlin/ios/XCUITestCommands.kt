package ios

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import org.json.JSONArray
import dtos.TestExecutionConfigDTO
import utils.Tools
import utils.Logger

object XCUITestCommands {

    /**
     * Runs the iOS app startup test and maps the result like Android.
     * Sends thresholdType and thresholdValue as query params if defined.
     */
    fun runAppStartupTest(config: TestExecutionConfigDTO): Map<String, String> {
        val deviceName = config.deviceName
        val bundleId = config.appPackage
        val targetElementId = config.metricParams["elementToWaitFor"]
            ?: error("Missing 'elementToWaitFor' parameter")
        val timeout = config.metricParams["timeout"]?.toIntOrNull() ?: 60

        // Find threshold for launchDuration/launchTime if present
        val threshold = config.testThresholds?.find { 
            it.third == "launchDuration" || it.third == "launchTime" 
        }
        val thresholdType = threshold?.second ?: ""
        val thresholdValue = threshold?.first ?: ""

        return try {
            val (launchTime, elementFound, success) = measureAppStartupTime(
                deviceName, bundleId, targetElementId, timeout, thresholdType, thresholdValue
            )
            mapOf(
                "launchTime" to launchTime,
                "elementFound" to elementFound,
                "success" to success
            )
        } catch (e: Exception) {
            Logger.error("❌ Error during iOS test: ${e.message}")
            mapOf(
                "launchTime" to "Not Found",
                "elementFound" to "False",
                "success" to "False"
            )
        }
    }

    /**
     * Calls the test server and returns a triple: (launchTime, elementFound, success)
     */
    fun measureAppStartupTime(
        deviceName: String,
        bundleId: String,
        elementId: String,
        timeoutInSeconds: Int,
        thresholdType: String = "",
        thresholdValue: String = ""
    ): Triple<String, String, String> {
        val driverRunnerPath = findDriverRunnerDir()
        val projectDir = driverRunnerPath
        val projectPath = File(projectDir, "DriverRunner.xcodeproj")
        val derivedDataPath = File(projectDir, "build")

        println("🧪 Waiting for element: \"$elementId\"")

        val bootedSimulatorId = Tools.getSimulatorIdforIOS(deviceName)
        val destination = "platform=iOS Simulator,id=$bootedSimulatorId"
        val command =
            listOf(
                "xcodebuild",
                "-project",
                projectPath.absolutePath,
                "-scheme",
                "DriverRunnerUITests",
                "-destination",
                destination,
                "-derivedDataPath",
                derivedDataPath.absolutePath,
                "COMMAND_BUNDLE_ID=$bundleId",
                "test"
            )

        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)
        val startedProcess = processBuilder.start()
        val outputThread = Thread {
            try {
                startedProcess.inputStream.bufferedReader().forEachLine { line ->
                    println("[xcodebuild] $line")
                }
            } catch (_: Exception) {
                // Ignore stream closed or other exceptions
            }
        }
        outputThread.start()

        val serverReady = waitForServer("http://localhost:4000/health", timeoutSeconds = 60)

        if (!serverReady) {
            println("❌ Test server did not become available at http://localhost:4000/health")
            startedProcess.destroy()
            outputThread.join()
            return Triple("Not Found", "False", "False")
        }

        // Add thresholdType and thresholdValue as query params if present
        val thresholdParams = if (thresholdType.isNotBlank() && thresholdValue.isNotBlank()) {
            "&thresholdType=$thresholdType&thresholdValue=$thresholdValue"
        } else {
            ""
        }

        val testUrl = "http://localhost:4000/test-launch?element=$elementId&timeout=$timeoutInSeconds$thresholdParams"
        val jsonResponse =
            try {
                sendGetRequestWithRetry(testUrl, retries = 5, delayMillis = 1000)
            } catch (e: Exception) {
                println("❌ Failed to call /test-launch: ${e.message}")
                startedProcess.destroy()
                outputThread.join()
                return Triple("Not Found", "False", "False")
            }

        println("✅ JSON Response:\n$jsonResponse")

        startedProcess.waitFor(timeoutInSeconds.toLong(), TimeUnit.SECONDS)
        outputThread.join()

        return try {
            val jsonArray = JSONArray(jsonResponse)
            val result = jsonArray.getJSONObject(0)
            val value = result.optString("value", "Not Found")
            val elementFound = result.optString("success", "False")
            val success = result.optString("success", "False")
            startedProcess.destroy()
            Triple(value, elementFound, success)
        } catch (e: Exception) {
            println("❌ Failed to parse test server response: ${e.message}")
            startedProcess.destroy()
            outputThread.join()
            return Triple("Not Found", "False", "False")
        }
    }

    private fun waitForServer(url: String, timeoutSeconds: Int): Boolean {
        val deadline = System.currentTimeMillis() + timeoutSeconds * 1000
        while (System.currentTimeMillis() < deadline) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 2000
                connection.requestMethod = "GET"
                connection.connect()
                if (connection.responseCode == 200) {
                    println("✅ Server is up!")
                    return true
                }
            } catch (_: Exception) {
                // Silence while waiting
            }
            Thread.sleep(1000)
        }
        return false
    }

    private fun sendGetRequest(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        return connection.inputStream.bufferedReader().readText()
    }

    private fun sendGetRequestWithRetry(url: String, retries: Int = 5, delayMillis: Long = 1000): String {
        repeat(retries - 1) {
            try {
                return sendGetRequest(url)
            } catch (e: Exception) {
                println("🔄 Retrying /test-launch: ${e.message}")
                Thread.sleep(delayMillis)
            }
        }
        // Last attempt, let exception propagate
        return sendGetRequest(url)
    }

    fun findDriverRunnerDir(): File {
        var current = File(".").canonicalFile

        while (current.parentFile != null) {
            val candidate = File(current, "DriverRunner")
            if (candidate.exists() && candidate.isDirectory) {
                return candidate.canonicalFile
            }
            current = current.parentFile
        }

        error(
            "❌ Diretório 'DriverRunner' não encontrado em nenhuma pasta acima de ${File(".").canonicalPath}"
        )
    }
}