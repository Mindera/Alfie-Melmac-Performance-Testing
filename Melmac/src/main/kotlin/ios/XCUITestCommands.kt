package ios

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import utils.Tools

object XCUITestCommands {

    fun runLaunchTestForElement(deviceName: String, bundleId: String, elementId: String): Boolean {
        val driverRunnerPath = findDriverRunnerDir()
        val projectDir = driverRunnerPath
        val projectPath = File(projectDir, "DriverRunner.xcodeproj")
        val derivedDataPath = File(projectDir, "build") 

        println("🧪 Waiting for element: \"$elementId\"")

        val bootedSimulatorId = Tools.getSimulatorIdforIOS(deviceName)

        val destination = "platform=iOS Simulator,id=$bootedSimulatorId"
        val command = listOf(
            "xcodebuild",
            "-project", projectPath.absolutePath,
            "-scheme", "DriverRunnerUITests",
            "-destination", destination,
            "-derivedDataPath", derivedDataPath.absolutePath,
            "COMMAND_BUNDLE_ID=$bundleId",
            "test"
        )

        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)
        val startedProcess = processBuilder.start()

        val outputThread = Thread {
            // Ignora completamente o output do processo
            startedProcess.inputStream.bufferedReader().forEachLine {}
        }
        outputThread.start()

        val serverReady = waitForServer("http://localhost:4000/health", timeoutSeconds = 60)

        if (!serverReady) {
            println("❌ Test server did not become available at http://localhost:4000/health")
            startedProcess.destroy()
            return false
        }

        val testUrl = "http://localhost:4000/test-launch?element=$elementId"
        val jsonResponse = try {
            sendGetRequest(testUrl)
        } catch (e: Exception) {
            println("❌ Failed to call /test-launch: ${e.message}")
            startedProcess.destroy()
            return false
        }

        println("✅ JSON Response:\n$jsonResponse")

        startedProcess.waitFor(60, TimeUnit.SECONDS)
        outputThread.join()

        return jsonResponse.contains("\"success\" : true")
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
                // Silenciar prints enquanto espera
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
