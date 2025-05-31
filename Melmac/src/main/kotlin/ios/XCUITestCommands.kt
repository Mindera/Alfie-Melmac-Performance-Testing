package ios

import dtos.TestExecutionConfigDTO
import java.io.File
import java.util.concurrent.TimeUnit
import org.json.JSONArray
import utils.Logger
import utils.Tools

object XCUITestCommands {

    fun runAppStartupTest(config: TestExecutionConfigDTO): Map<String, String> {
        val deviceName = config.deviceName
        val targetElementId =
            config.metricParams["elementToWaitFor"] ?: error("Missing 'elementToWaitFor' parameter")
        val timeout = config.metricParams["timeout"]?.toIntOrNull() ?: 60

        val threshold =
            config.testThresholds?.find { it.third == "launchDuration" || it.third == "launchTime" }
        val thresholdType = threshold?.second ?: ""
        val thresholdValue = threshold?.first ?: ""
        val bundleId = config.appPackage

        return try {
            val (launchTime, elementFound, success) = measureAppStartupTime(
                deviceName,
                targetElementId,
                timeout,
                thresholdType,
                thresholdValue,
                testName = "DriverRunnerUITests/DriverRunnerUITests/testAppStartup",
                bundleId = bundleId
            )
            mapOf("launchTime" to launchTime, "elementFound" to elementFound, "success" to success)
        } catch (e: Exception) {
            Logger.error("❌ Error during iOS test: ${e.message}")
            mapOf("launchTime" to "Not Found", "elementFound" to "False", "success" to "False")
        }
    }

    fun measureAppStartupTime(
        deviceName: String,
        elementId: String,
        timeoutInSeconds: Int,
        thresholdType: String = "",
        thresholdValue: String = "",
        testName: String,
        bundleId: String
    ): Triple<String, String, String> {
        val driverRunnerPath = findDriverRunnerDir()
        val projectDir = driverRunnerPath
        val bootedSimulatorId = Tools.getSimulatorIdforIOS(deviceName)

        val envVars = mapOf(
            "COMMAND_BUNDLE_ID" to bundleId,
            "TEST_ELEMENT" to elementId,
            "TEST_TIMEOUT" to timeoutInSeconds.toString(),
            "TEST_THRESHOLD_TYPE" to thresholdType,
            "TEST_THRESHOLD_VALUE" to thresholdValue
        )

        val command = buildXcodeCommand(
            projectDir = projectDir,
            simulatorId = bootedSimulatorId,
            testName = testName,
            envVars = envVars
        )

        println("🚀 Running command: ${command.joinToString(" ")}")

        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)
        val startedProcess = processBuilder.start()

        // Create a buffer for output
        val outputBuffer = StringBuilder()
        
        // Read output in real-time
        val outputThread = Thread {
            startedProcess.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    println("[xcodebuild] $line")
                    outputBuffer.append(line).append("\n")
                }
            }
        }
        outputThread.start()

        // Wait for process to complete with timeout
        val completed = startedProcess.waitFor(timeoutInSeconds.toLong(), TimeUnit.SECONDS)
        outputThread.join(5000)

        if (!completed) {
            Logger.error("❌ xcodebuild process timed out after $timeoutInSeconds seconds")
            Logger.error("Full output before timeout:\n$outputBuffer") 
            startedProcess.destroyForcibly()
            return Triple("Not Found", "False", "False")
        }

        val exitCode = startedProcess.exitValue()
        if (exitCode != 0) {
            Logger.error("❌ xcodebuild process failed with exit code: $exitCode")
            Logger.error("Full output:\n$outputBuffer")
        }

        // Extract JSON from output
        val jsonText = outputBuffer.toString().substringAfter("✅ Result JSON:", "").trim()
        if (jsonText.isEmpty()) {
            Logger.error("❌ JSON output not found in xcodebuild output")
            Logger.error("Full output:\n$outputBuffer")
            return Triple("Not Found", "False", "False")
        }

        return try {
            val jsonArray = JSONArray(jsonText)
            val result = jsonArray.getJSONObject(0)
            val value = result.optString("value", "Not Found")
            val elementFound = result.optString("elementFound", "False")
            val success = result.optString("success", "False")
            Triple(value, elementFound, success)
        } catch (e: Exception) {
            Logger.error("❌ Failed to parse JSON: ${e.message}")
            Logger.error("JSON text was: $jsonText")
            Triple("Not Found", "False", "False")
        }
    }

    private fun buildXcodeCommand(
        projectDir: File,
        simulatorId: String,
        testName: String,
        envVars: Map<String, String>
    ): List<String> {
        val projectPath = File(projectDir, "DriverRunner.xcodeproj")
        val derivedDataPath = File(projectDir, "build")
        val destination = "platform=iOS Simulator,id=$simulatorId"

        val command = mutableListOf(
            "xcodebuild",
            "-project",
            projectPath.absolutePath,
            "-scheme",
            "DriverRunnerUITests",
            "-destination",
            destination,
            "-derivedDataPath",
            derivedDataPath.absolutePath
        )

        envVars.forEach { (key, value) ->
            command.add("$key=$value")
        }

        command.add("-only-testing:$testName")
        command.add("test")

        return command
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
        error("❌ Diretório 'DriverRunner' não encontrado em nenhuma pasta acima de ${File(".").canonicalPath}")
    }
}
